package com.bridge.console.service.operatelog.impl;

import com.bridge.console.model.dao.*;
import com.bridge.console.model.entity.AppDefDO;
import com.bridge.console.model.entity.ConfigDO;
import com.bridge.console.model.entity.ConfigOperateLogDO;
import com.bridge.console.model.enums.AccountRoleEnum;
import com.bridge.console.model.enums.KeyStatusEnum;
import com.bridge.console.model.enums.OperateTypeEnum;
import com.bridge.console.model.vo.AppDefVO;
import com.bridge.console.model.vo.AppListQuery;
import com.bridge.console.model.vo.WorkSpaceVO;
import com.bridge.console.service.config.ConfigService;
import com.bridge.console.service.operatelog.OperateLogService;
import com.bridge.console.service.zk.ZookeeperComponent;
import com.bridge.console.utils.BeanUtil;
import com.bridge.console.utils.EnumUtil;
import com.bridge.console.utils.ex.BusinessCheckFailException;
import com.bridge.console.utils.result.BaseBizEnum;
import com.bridge.console.utils.result.BaseErrorEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * @author Jay
 * @version v1.0
 * @description 操作日志
 * @date 2019-02-20 14:55
 */
@Service
@Slf4j
public class OperateLogServiceImpl implements OperateLogService {

    @Autowired
    private ConfigOperateLogMapper configOperateLogMapper;

    @Autowired
    private UserAccountMapper userAccountMapper;

    @Autowired
    private AppDefMapper appDefMapper;

    @Autowired
    private TeamDefMapper teamDefMapper;

    @Autowired
    private ConfigMapper configMapper;

    @Autowired
    private ZookeeperComponent zookeeperComponent;

    @Autowired
    private ConfigService configService;

    @Autowired
    private OperateLogService operateLogService;

    /**
     * 回滚数据
     *
     * @param operatorId
     * @param id
     * @param roleType
     * @param operatorTeamId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean rollbackData(Integer id, Integer operatorId, Integer roleType, Integer operatorTeamId) {
        ConfigOperateLogDO configOperateLogDO = configOperateLogMapper.selectByPrimaryKey(id);
        if (configOperateLogDO == null) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "数据过期，请刷新页面");
        }
        Integer operateType = configOperateLogDO.getOperateType();
        if (operateType == null || !EnumUtil.getKeys(OperateTypeEnum.class).contains(operateType)) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "操作状态不合法，数据异常！");
        }
        // 操作权限校验
        configService.permissionCheck(operatorId, configOperateLogDO.getAppId(), roleType, operatorTeamId);
        OperateTypeEnum operateTypeEnum = EnumUtil.getEnum(operateType, OperateTypeEnum.class);
        if (operateTypeEnum == null) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "操作状态不合法，数据异常！");
        }
        String valueBefore = configOperateLogDO.getValueBefore();
        // 第一次下发的不回滚，即操作前的值为空的不回滚，因为可以选择删除这个配置项，所以不提供回滚
        // 操作操作类型为'删除已下发的'的记录时，需要判断操作前的值是否为空
        if (StringUtils.isEmpty(valueBefore)) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "无法回滚第一次下发的配置项");
        }
        // 从db中查询出要回滚的对应的原始记录
        ConfigDO configDoFromDb = this.queryFromDb(configOperateLogDO);
        ConfigDO configDoForOperate;
        // 数据库里面的记录如果存在，则更新
        if (configDoFromDb != null) {
            // 基于原始数据组装数据用于回滚
            configDoForOperate = this.convertToConfigDoForUpdate(configOperateLogDO, configDoFromDb, operatorId);
            if (configMapper.updateById(configDoForOperate) != BaseBizEnum.FIRST.getCode()) {
                throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "数据回滚失败");
            }
        } else {
            // 数据库里面的记录如果不存在，则新增一条
            configDoForOperate = this.fillConfigDoForInsert(configOperateLogDO, operatorId);
            if (configMapper.insertSelective(configDoForOperate) != BaseBizEnum.FIRST.getCode()) {
                throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "数据回滚失败");
            }
        }
        // key节点如果不存在则创建,存在则更新，返回key的节点路径
        String configKeyNodePath = zookeeperComponent.createConfigKeyNodeIfNotExistOrUpdate(configDoForOperate);
        // 这里直接全量回滚
        List<String> machineList = zookeeperComponent.getChildrenPath(configKeyNodePath);
        // 如果machine节点都为空，则不通知客户端,等待客户端自己注册，【这种情况一般是第一次下发 或者是 被删除后的key的回滚】
        if (!CollectionUtils.isEmpty(machineList)) {
            // 通知zk
            configService.notifyZookeeper(machineList, configDoForOperate);
        }
        // 操作人下发次数自增1
        configService.increasePushCount(operatorId);
        // 记日志
        this.writeLogWhenRollBack(configDoForOperate, configDoFromDb);
        return Boolean.TRUE;
    }


    /**
     * 写入日志表
     *
     * @param configOperateLogDO
     */
    @Override
    public void writeLog(ConfigOperateLogDO configOperateLogDO) {
        if (configOperateLogDO == null) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "参数不能为空");
        }
        Date date = new Date();
        configOperateLogDO.setIsDeleted(BaseBizEnum.YN_N.getCode());
        configOperateLogDO.setGmtModified(date);
        configOperateLogDO.setGmtCreate(date);
        if (configOperateLogMapper.insertSelective(configOperateLogDO) != BaseBizEnum.FIRST.getCode()) {
            log.error("写入日志失败,入参为:{}", configOperateLogDO);
        }
    }


    /**
     * 查询工作台数据
     *
     * @param accountId
     * @param accountRoleEnum
     * @param teamId
     * @return
     */
    @Override
    public WorkSpaceVO getWorkSpaceInfo(Integer teamId, Integer accountId, AccountRoleEnum accountRoleEnum, Integer envId) {
        WorkSpaceVO workSpaceVO = new WorkSpaceVO();
        AppListQuery appListQuery = new AppListQuery();
        switch (accountRoleEnum) {
            case ROLE_USER:
                workSpaceVO.setPushCount(userAccountMapper.selectPushCount(null, accountId));
                appListQuery.setTeamId(teamId);
                appListQuery.setAppOwner(accountId);
                break;
            case ROLE_ADMIN:
                workSpaceVO.setPushCount(userAccountMapper.selectPushCount(null, null));
                appListQuery.setTeamId(null);
                break;
            case ROLE_TEAM_LEADER:
                workSpaceVO.setPushCount(userAccountMapper.selectPushCount(teamId, null));
                appListQuery.setTeamId(teamId);
                break;
            default:
                break;
        }
        workSpaceVO.setProjectNumber(appDefMapper.countAppList(appListQuery));
        // 这里只查询4条
        appListQuery.setEnvId(envId);
        appListQuery.setLimit(4);
        List<AppDefDO> appDefDoList = appDefMapper.queryWorkSpaceAppList(appListQuery);
        if (!CollectionUtils.isEmpty(appDefDoList)) {
            List<AppDefVO> appDefVoList = BeanUtil.copyList(appDefDoList, AppDefVO.class);
            appDefVoList.forEach(appDefVO -> {
                        appDefVO.setTeamName(teamDefMapper.selectTeamNameById(appDefVO.getTeamId()));
                        appDefVO.setOwnerRealName(userAccountMapper.selectRealNameById(appDefVO.getAppOwner()));
                    }
            );
            workSpaceVO.setAppDefVoList(appDefVoList);
        }
        return workSpaceVO;
    }


    //--------------------------------------private method------------------------------------------------


    /**
     * 转换
     *
     * @param configDoFromDb
     * @param operateId
     * @param configOperateLogDO
     * @return
     */
    private ConfigDO convertToConfigDoForUpdate(ConfigOperateLogDO configOperateLogDO, ConfigDO configDoFromDb,
                                                Integer operateId) {
        ConfigDO configDO = new ConfigDO();
        BeanUtil.copyProperties(configDoFromDb, configDO);
        configDO.setGmtModified(new Date());
        configDO.setModifier(operateId);
        configDO.setKeyVersion(configOperateLogDO.getVersionBefore());
        configDO.setConfigValue(configOperateLogDO.getValueBefore());
        configDO.setPreConfigValue("");
        return configDO;
    }


    /**
     * 从数据库中查询原始记录
     *
     * @param configOperateLogDO
     * @return
     */
    private ConfigDO queryFromDb(ConfigOperateLogDO configOperateLogDO) {
        return configMapper.selectByAppIdAndEnvIdAndConfigKey(configOperateLogDO.getConfigKey()
                , configOperateLogDO.getAppId(), configOperateLogDO.getEnvId());
    }


    /**
     * 组装参数
     *
     * @param configDoForOperate
     * @param configDoFromDb
     */
    private void writeLogWhenRollBack(ConfigDO configDoForOperate, ConfigDO configDoFromDb) {
        if (configDoForOperate == null) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "数据异常");
        }
        ConfigOperateLogDO configOperateLogDO = new ConfigOperateLogDO();
        Date date = new Date();
        Integer operatorId = configDoForOperate.getModifier();
        configOperateLogDO.setGmtCreate(date);
        configOperateLogDO.setCreator(operatorId);
        configOperateLogDO.setGmtModified(date);
        configOperateLogDO.setModifier(operatorId);
        configOperateLogDO.setIsDeleted(BaseBizEnum.YN_N.getCode());
        if (configDoFromDb != null) {
            configOperateLogDO.setValueBefore(configDoFromDb.getConfigValue());
            configOperateLogDO.setVersionBefore(configDoFromDb.getKeyVersion());
        } else {
            configOperateLogDO.setValueBefore(configDoForOperate.getConfigValue());
            configOperateLogDO.setVersionBefore(configDoForOperate.getKeyVersion());
        }
        configOperateLogDO.setConfigKey(configDoForOperate.getConfigKey());
        configOperateLogDO.setVersionAfter(configDoForOperate.getKeyVersion());
        configOperateLogDO.setValueAfter(configDoForOperate.getConfigValue());
        configOperateLogDO.setAppId(configDoForOperate.getAppId());
        configOperateLogDO.setOperateId(operatorId);
        configOperateLogDO.setOperateName(userAccountMapper.selectRealNameById(operatorId));
        configOperateLogDO.setEnvId(configDoForOperate.getEnvId());
        configOperateLogDO.setOperateType(OperateTypeEnum.ROLL_BACK.getKey());
        configDoForOperate.setKeyDes(configDoForOperate.getKeyDes());
        operateLogService.writeLog(configOperateLogDO);
    }

    /**
     * 参数填充
     *
     * @param configOperateLogDO
     * @param operatorId
     * @return
     */
    private ConfigDO fillConfigDoForInsert(ConfigOperateLogDO configOperateLogDO, Integer operatorId) {
        ConfigDO configDO = new ConfigDO();
        Date date = new Date();
        configDO.setGmtModified(date);
        configDO.setGmtCreate(date);
        configDO.setModifier(operatorId);
        configDO.setCreator(operatorId);
        configDO.setIsDeleted(BaseBizEnum.YN_N.getCode());
        configDO.setKeyStatus(KeyStatusEnum.ENABLED.getKey());
        configDO.setKeyVersion(configOperateLogDO.getVersionBefore());
        configDO.setConfigKey(configOperateLogDO.getConfigKey());
        configDO.setConfigValue(configOperateLogDO.getValueBefore());
        configDO.setAppId(configOperateLogDO.getAppId());
        configDO.setEnvId(configOperateLogDO.getEnvId());
        configDO.setKeyDes(configOperateLogDO.getKeyDes());
        return configDO;
    }
}
