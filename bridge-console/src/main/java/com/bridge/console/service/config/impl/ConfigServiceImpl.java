package com.bridge.console.service.config.impl;

import com.bridge.console.model.constant.Constant;
import com.bridge.console.model.dao.AppDefMapper;
import com.bridge.console.model.dao.ConfigMapper;
import com.bridge.console.model.dao.TeamDefMapper;
import com.bridge.console.model.dao.UserAccountMapper;
import com.bridge.console.model.entity.*;
import com.bridge.console.model.enums.*;
import com.bridge.console.model.vo.*;
import com.bridge.console.service.config.ConfigService;
import com.bridge.console.service.operatelog.OperateLogService;
import com.bridge.console.service.zk.ZookeeperComponent;
import com.bridge.console.utils.BeanUtil;
import com.bridge.console.utils.EnumUtil;
import com.bridge.console.utils.ex.BusinessCheckFailException;
import com.bridge.console.utils.ex.BusinessProcessFailException;
import com.bridge.console.utils.result.BaseBizEnum;
import com.bridge.console.utils.result.BaseErrorEnum;
import com.bridge.domain.ConfigDTO;
import com.bridge.domain.ConfigKv;
import com.bridge.domain.Constants;
import com.bridge.enums.EnvEnum;
import com.bridge.enums.NeedUpdateEnum;
import com.bridge.utils.DateUtils;
import com.bridge.utils.NodePathUtils;
import com.bridge.utils.PropUtils;
import com.bridge.zookeeper.data.ConfigKeyNodeData;
import com.bridge.zookeeper.data.MachineNodeData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.bridge.console.model.constant.Constant.MSIE;
import static com.bridge.console.model.constant.Constant.TRIDENT;

/**
 * @author Jay
 * @version v1.0
 * @description 配置项服务
 * @date 2019-01-28 14:44
 */
@Slf4j
@Service
public class ConfigServiceImpl implements ConfigService {

    @Autowired
    private TeamDefMapper teamDefMapper;

    @Autowired
    private AppDefMapper appDefMapper;

    @Autowired
    private ConfigMapper configMapper;

    @Autowired
    private ZookeeperComponent zookeeperComponent;

    @Autowired
    private OperateLogService operateLogService;

    @Autowired
    private UserAccountMapper userAccountMapper;

    /**
     * 获取选项列表,如果是管理员则获取所有的，如果是团队leader则获取自己团队下的
     *
     * @param teamId
     * @return
     */
    @Override
    public List<ConfigSelectorVO> getSelectorData(Integer teamId) {
        List<TeamDefDO> teamDefDoList = teamDefMapper.queryTeamList(teamId);
        if (CollectionUtils.isEmpty(teamDefDoList)) {
            return null;
        }
        List<ConfigSelectorVO> configSelectorVoList = new ArrayList<>();
        teamDefDoList.forEach(teamDefDO -> {
            if (teamDefDO == null) {
                return;
            }
            ConfigSelectorVO configSelectorVO = new ConfigSelectorVO();
            configSelectorVO.setTeamId(teamDefDO.getId());
            configSelectorVO.setTeamName(teamDefDO.getTeamName());
            // 查询该团队下的系统
            AppListQuery appListQuery = new AppListQuery();
            appListQuery.setTeamId(teamDefDO.getId());
            List<AppDefDO> appDefDoList = appDefMapper.queryAppList(appListQuery);
            List<ConfigAppVO> configAppVoList = new ArrayList<>();
            if (!CollectionUtils.isEmpty(appDefDoList)) {
                appDefDoList.forEach(appDefDO -> {
                    if (appDefDO == null) {
                        return;
                    }
                    ConfigAppVO configAppVO = new ConfigAppVO();
                    configAppVO.setAppId(appDefDO.getId());
                    configAppVO.setAppCode(appDefDO.getAppCode());
                    configAppVO.setAppName(appDefDO.getAppName());
                    configAppVoList.add(configAppVO);
                });
            }
            configSelectorVO.setConfigAppList(configAppVoList);
            configSelectorVoList.add(configSelectorVO);
        });
        return configSelectorVoList;
    }


    /**
     * 新增kv
     *
     * @param configDataVO
     * @param operatorId
     * @param teamId
     * @param roleType
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean addConfigKv(ConfigDataVO configDataVO, Integer operatorId, Integer roleType, Integer teamId) {
        // 参数校验
        this.checkConfigDataVO(configDataVO, true);
        // 操作权限校验
        this.permissionCheck(operatorId, configDataVO.getAppId(), roleType, teamId);
        // 判断key是否已经存在
        if (configMapper.judgeKeyIsExist(configDataVO.getConfigKey(), configDataVO.getAppId(),
                configDataVO.getEnvId()) > BaseBizEnum.ZERO.getCode()) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "该配置项已存在");
        }
        // 保存key
        ConfigDO configDO = new ConfigDO();
        Date date = new Date();
        BeanUtil.copyProperties(configDataVO, configDO);
        configDO.setGmtCreate(date);
        configDO.setGmtModified(date);
        configDO.setCreator(operatorId);
        configDO.setModifier(operatorId);
        configDO.setKeyVersion(DateUtils.getVersion());
        configDO.setKeyStatus(KeyStatusEnum.NOT_ENABLED.getKey());
        configDO.setIsDeleted(BaseBizEnum.YN_N.getCode());
        if (configMapper.insertSelective(configDO) != BaseBizEnum.FIRST.getCode()) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "保存失败");
        }
        return Boolean.TRUE;
    }

    /**
     * 编辑kv
     *
     * @param configDataVO
     * @param operatorId
     * @param teamId
     * @param roleType
     * @return
     */
    @Override
    public Boolean editConfigKv(ConfigDataVO configDataVO, Integer operatorId, Integer roleType, Integer teamId) {
        // 参数校验
        this.checkConfigDataVO(configDataVO, false);
        this.permissionCheck(operatorId, configDataVO.getAppId(), roleType, teamId);
        // 更新
        ConfigDO configDO = new ConfigDO();
        Date date = new Date();
        BeanUtil.copyProperties(configDataVO, configDO);
        configDO.setGmtModified(date);
        configDO.setModifier(operatorId);
        // 先去查询这条记录，判断是否有生效值
        ConfigDO configDoFromDb = configMapper.selectById(configDataVO.getId());
        if (configDoFromDb == null) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "数据异常");
        }
        // 无生效值则是未生效
        if (StringUtils.isEmpty(configDoFromDb.getConfigValue())) {
            configDO.setKeyStatus(KeyStatusEnum.NOT_ENABLED.getKey());
        } else {
            // 否则就是已生效
            configDO.setKeyStatus(KeyStatusEnum.ENABLED.getKey());
        }
        if (configMapper.updateConfigKv(configDO) != BaseBizEnum.FIRST.getCode()) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "更新失败");
        }
        return Boolean.TRUE;
    }


    /**
     * 下发k/v至客户端
     *
     * @param pushConfigKvVO
     * @param operatorId
     * @param roleType
     * @param teamId
     * @return
     */
    @Override
    public synchronized Boolean pushConfigKv(PushConfigKvVO pushConfigKvVO, Integer operatorId,
                                             Integer roleType, Integer teamId) {
        // 参数校验
        this.checkPushConfigKeyVO(pushConfigKvVO);
        // 查询记录
        ConfigDO configDoFromDb = configMapper.selectById(pushConfigKvVO.getId());
        if (configDoFromDb == null || configDoFromDb.getEnvId() == null) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "数据异常, 请刷新页面后重试");
        }
        // 权限校验
        this.permissionCheck(operatorId, pushConfigKvVO.getAppId(), roleType, teamId);
        // DO转换，为了更新操作
        ConfigDO configDoForUpdate = this.convertToConfigDoForUpdate(configDoFromDb, operatorId);
        // 预备值不为空的话就将预备值替换为生效值
        if (!StringUtils.isEmpty(configDoFromDb.getPreConfigValue())) {
            // 更新
            configDoForUpdate.setKeyVersion(DateUtils.getVersion());
            configDoForUpdate.setConfigValue(configDoFromDb.getPreConfigValue());
            if (configMapper.updateById(configDoForUpdate) != BaseBizEnum.FIRST.getCode()) {
                throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "数据更新失败");
            }
        }
        // key节点如果不存在则创建，返回key的节点路径
        String configKeyNodePath = zookeeperComponent.createConfigKeyNodeIfNotExistOrUpdate(configDoForUpdate);
        // 获取machine节点的路径
        List<String> machineList = this.getMachineListByPushType(pushConfigKvVO, configKeyNodePath);
        // 如果machine节点都为空，则不通知客户端,等待客户端自己注册，这种情况一般是第一次下发
        if (!CollectionUtils.isEmpty(machineList)) {
            // 通知zk
            this.notifyZookeeper(machineList, configDoForUpdate);
        }
        // 下发次数自增1
        this.increasePushCount(operatorId);
        // 写入日志记录
        this.writeLogWhenPush(configDoFromDb, configDoForUpdate);
        return Boolean.TRUE;
    }


    /**
     * 一键下发k/v至客户端，全量下发
     *
     * @param appId
     * @param envId
     * @param operatorId
     * @param roleType
     * @param teamId
     * @return
     */
    @Override
    public Boolean pushAllConfigKv(Integer appId, Integer envId, Integer operatorId, Integer roleType, Integer teamId) {
        PushConfigKvVO pushConfigKvVO = new PushConfigKvVO();
        pushConfigKvVO.setAppId(appId);
        pushConfigKvVO.setPushType(PushTypeEnum.FULL.getKey());
        List<Integer> configKeyList = configMapper.selectConfigKeyIdList(appId, envId);
        if (CollectionUtils.isEmpty(configKeyList)) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "未查询到配置项");
        }
        configKeyList.forEach(id -> {
            if (id == null) {
                return;
            }
            pushConfigKvVO.setId(id);
            this.pushConfigKv(pushConfigKvVO, operatorId, roleType, teamId);
        });
        return Boolean.TRUE;
    }

    /**
     * 通知zk
     *
     * @param machineList
     * @param configDO
     */
    @Override
    public void notifyZookeeper(List<String> machineList, ConfigDO configDO) {
        String appCode = appDefMapper.selectAppCodeById(configDO.getAppId());
        Integer envId = configDO.getEnvId();
        if (StringUtils.isEmpty(appCode) || envId == null) {
            log.error("未查询到对应的数据, configDO : {}", configDO);
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "数据异常");
        }
        // 获取key的zk路径
        String configKeyNodePath = NodePathUtils.getConfigKeyNodePath(appCode, configDO.getConfigKey(), EnvEnum.getEnvEnum(envId));
        ConfigKeyNodeData configKeyNodeData = this.fillConfigKeyNodeData(configDO);
        // 如果传入的machine节点不存在
        if (CollectionUtils.isEmpty(machineList)) {
            // key节点存在,则更新key节点,否则更新key节点
            if (zookeeperComponent.checkNodeIsExist(configKeyNodePath)) {
                zookeeperComponent.updateConfigKeyNodeData(configKeyNodePath, configKeyNodeData);
            } else {
                zookeeperComponent.createConfigKeyNode(configKeyNodePath, configKeyNodeData);
            }
            return;
        }
        // 如果传入的machine节点存在 && key节点存在
        if (zookeeperComponent.checkNodeIsExist(configKeyNodePath)) {
            // 校验传入的machine节点是否存在
            machineList.forEach(host -> {
                String machineNodePath = NodePathUtils.getMachineNodePath(configKeyNodePath, host);
                if (zookeeperComponent.checkNodeIsExist(machineNodePath)) {
                    MachineNodeData machineNodeData = zookeeperComponent.getMachineNodeData(machineNodePath);
                    machineNodeData.setNeedUpdate(NeedUpdateEnum.NEED_UPDATE.getKey());
                    zookeeperComponent.updateMachineNodeData(machineNodePath, machineNodeData);
                } else {
                    log.warn("machine节点 :{} 不存在", machineNodePath);
                }
            });
            zookeeperComponent.updateConfigKeyNodeData(configKeyNodePath, configKeyNodeData);
        } else {
            // 如果传入的machine节点存在 && key节点不存在，那么属于数据异常了
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "数据过期请刷新页面");
        }
    }

    /**
     * 获取客户端节点数据
     *
     * @param appCode
     * @param configKey
     * @param envId
     * @return
     */
    @Override
    public List<MachineNodeData> getMachineNodeDataListByConfigKey(String appCode, String configKey, Integer envId) {
        if (StringUtils.isEmpty(appCode)) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "appCode不能为空");
        }
        if (StringUtils.isEmpty(configKey)) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "configKey不能为空");
        }
        String configKeyNodePath = NodePathUtils.getConfigKeyNodePath(appCode, configKey, EnvEnum.getEnvEnum(envId));
        return zookeeperComponent.getMachineNodeDataList(configKeyNodePath);
    }

    /**
     * 查询key的集合列表
     *
     * @param appId
     * @param envId
     * @return
     */
    @Override
    public List<ValueVO> queryConfigKeyList(Integer appId, Integer envId) {
        if (appId == null) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "appId不能为空");
        }
        return configMapper.selectConfigKeyList(appId, envId);
    }


    /**
     * 根据uniqueToken查询配置列表
     *
     * @param uniqueToken
     * @return
     */
    @Override
    public ConfigDTO getConfigList(String uniqueToken) {
        if (StringUtils.isEmpty(uniqueToken)) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "uniqueToken不能为空");
        }
        AppDefDO appDefDO = appDefMapper.selectByAppCode(uniqueToken);
        if (appDefDO == null || appDefDO.getId() == null) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "系统不存在");
        }
        List<ConfigDO> configDoList = configMapper.selectByAppId(appDefDO.getId());
        if (!CollectionUtils.isEmpty(configDoList)) {
            ConfigDTO configDTO = new ConfigDTO();
            List<ConfigKv> configKvList = new ArrayList<>();
            configDoList.forEach(configDO -> {
                if (configDO == null) {
                    return;
                }
                ConfigKv configKv = new ConfigKv();
                configKv.setKey(configDO.getConfigKey());
                configKv.setValue(configDO.getConfigValue());
                configKv.setVersion(configDO.getKeyVersion());
                configKvList.add(configKv);
            });
            configDTO.setConfigList(configKvList);
        }
        return null;
    }

    /**
     * 删除key
     *
     * @param id
     * @param teamId
     * @param roleType
     * @param operatorId
     * @return
     */
    @Override
    public Boolean deleteConfigKey(Integer id, Integer operatorId, Integer roleType, Integer teamId) {
        if (id == null) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "id 不能为空");
        }
        int operateType = OperateTypeEnum.DEL_PUSH.getKey();
        // 查询记录
        ConfigDO configDO = configMapper.selectById(id);
        if (configDO == null) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "记录不存在");
        }
        this.permissionCheck(operatorId, configDO.getAppId(), roleType, teamId);
        // 配置项的值为空说明是还未下发，即未生效
        if (StringUtils.isEmpty(configDO.getConfigValue())) {
            operateType = OperateTypeEnum.DEL_NOT_PUSH.getKey();
        }
        // 删除数据库的记录
        if (configMapper.deletedById(operatorId, id) != 1) {
            throw new BusinessCheckFailException(BaseErrorEnum.UPDATE_ERROR.getCode(), "删除记录失败");
        }
        // 判断key节点是否存在，不存在则创建
        String appCode = appDefMapper.selectAppCodeById(configDO.getAppId());
        String configKeyNodePath
                = NodePathUtils.getConfigKeyNodePath(appCode, configDO.getConfigKey(), EnvEnum.getEnvEnum(configDO.getEnvId()));
        // 删除zk的节点
        zookeeperComponent.deletingChildrenIfNeeded(configKeyNodePath);
        // 写入日志记录
        writeLogWhenDel(configDO, configDO.getEnvId(), operatorId, operateType);
        return Boolean.TRUE;
    }

    /**
     * 根据appId查询
     *
     * @param appId
     * @param envId
     * @return
     */
    @Override
    public List<ZkDataVO> queryZkDataList(Integer appId, Integer envId) {
        if (appId == null) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "appId 不能为空");
        }
        if (envId == null) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "envId 不能为空");
        }
        String appNodePath = NodePathUtils.getAppNodePath(appDefMapper.selectAppCodeById(appId), EnvEnum.getEnvEnum(envId));
        List<ConfigKeyNodeData> configKeyNodeDataList = zookeeperComponent.getConfigKeyNodeDataList(appNodePath);
        if (CollectionUtils.isEmpty(configKeyNodeDataList)) {
            return null;
        }
        List<ZkDataVO> zkDataVoList = BeanUtil.copyList(configKeyNodeDataList, ZkDataVO.class);
        zkDataVoList.forEach(zkDataVO -> {
            String configKeyNodePath = NodePathUtils.getConfigKeyNodePathByAppNodePathAndConfigKey(appNodePath, zkDataVO.getKey());
            List<MachineNodeData> machineNodeDataList = zookeeperComponent.getMachineNodeDataList(configKeyNodePath);
            if (!CollectionUtils.isEmpty(machineNodeDataList)) {
                zkDataVO.setMachineNodeDataList(machineNodeDataList);
            } else {
                zkDataVO.setMachineNodeDataList(new ArrayList<>());
            }
        });
        zkDataVoList.sort(Comparator.comparing(ZkDataVO::getKey));
        return zkDataVoList;
    }

    /**
     * 下发次数自增1
     *
     * @param operatorId
     */
    @Override
    public void increasePushCount(Integer operatorId) {
        userAccountMapper.increasePushCount(operatorId);
    }


    /**
     * 判断是否有权限操作配置项，包括编辑、新增、下发、回滚
     *
     * @param operatorId
     * @param appId
     * @param roleType
     * @param operatorTeamId
     */
    @Override
    public void permissionCheck(Integer operatorId, Integer appId, Integer roleType, Integer operatorTeamId) {
        AppDefDO appDefDO = appDefMapper.selectByPrimaryKey(appId);
        if (appDefDO == null || appDefDO.getAppOwner() == null || appDefDO.getTeamId() == null || operatorTeamId == null) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "数据过期，请刷新页面");
        }
        // 系统管理员可以操作全部
        if (roleType == AccountRoleEnum.ROLE_ADMIN.getKey()) {
            return;
        }
        // 团队管理员只能操作自己的团队下的项目
        if (roleType == AccountRoleEnum.ROLE_TEAM_LEADER.getKey()) {
            if (!operatorTeamId.equals(appDefDO.getTeamId())) {
                throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "权限不够，无法操作！");
            }
            return;
        }
        // 普通用户只能操作是自己负责的项目
        if (roleType == AccountRoleEnum.ROLE_USER.getKey()) {
            if (!appDefDO.getAppOwner().equals(operatorId)) {
                throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "权限不够，无法操作！");
            }
            return;
        }
        throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "账号角色非法");
    }

    /**
     * 导出文件
     *
     * @param configListQuery
     * @param response
     * @param request
     * @throws IOException
     */
    @Override
    public void exportFile(ConfigListQuery configListQuery, HttpServletResponse response, HttpServletRequest request)
            throws IOException {
        List<ConfigDO> configList = configMapper.queryConfigKeyList(configListQuery);
        if (CollectionUtils.isEmpty(configList)) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "数据为空");
        }
        String appName = appDefMapper.selectAppNameById(configListQuery.getAppId());
        String env = getEnv(configListQuery.getEnvId());
        String fileName = appName.concat("-").concat(env).concat(".properties");
        // 避免乱码
        String userAgent = request.getHeader("User-Agent");
        // 针对IE或者以IE为内核的浏览器：
        if (userAgent.contains(MSIE) || userAgent.contains(TRIDENT)) {
            fileName = URLEncoder.encode(fileName, "UTF-8");
        } else {
            // 非IE浏览器的处理：
            fileName = new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
        }
        response.setHeader("Content-disposition", String.format("attachment; filename=\"%s\"", fileName));
        response.setCharacterEncoding("UTF-8");
        ServletOutputStream out = response.getOutputStream();
        configList.forEach(configDO -> {
            try {
                if (configDO == null || StringUtils.isEmpty(configDO.getConfigValue())) {
                    return;
                }
                String des = "# ".concat(configDO.getKeyVersion());
                String data = configDO.getConfigKey().concat(" = ").concat(configDO.getConfigValue());
                out.write(des.getBytes(StandardCharsets.UTF_8));
                out.write("\r\n".getBytes());
                out.write(data.getBytes(StandardCharsets.UTF_8));
                out.write("\r\n".getBytes());
                out.write("\r\n".getBytes());
            } catch (IOException e) {
                throw new BusinessCheckFailException(BaseErrorEnum.BNS_PRS_ERROR.getCode(), e.getMessage());
            }
        });
        out.flush();
        out.close();
    }

    /**
     * 查询消费者订阅情况
     *
     * @param appId
     * @param envId
     * @return
     */
    @Override
    public List<String> queryConsumerHost(Integer appId, Integer envId) {
        if (appId == null || envId == null) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "请先选择系统");
        }
        if (EnvEnum.getEnvEnum(envId) == null) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "环境不合法");
        }
        String appCode = appDefMapper.selectAppCodeById(appId);
        if (StringUtils.isEmpty(appCode)) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "系统不存在");
        }
        String path = NodePathUtils.getConsumerHostNode(appCode, EnvEnum.getEnvEnum(envId));
        if (zookeeperComponent.checkNodeIsExist(path)) {
            return zookeeperComponent.getChildrenPath(path);
        }
        return new ArrayList<>();
    }

    /**
     * 解析文件，持久化到db
     *
     * @param inputStream
     * @param appId
     * @param envId
     * @return
     */
    @Transactional(rollbackFor = BusinessProcessFailException.class)
    @Override
    public String parseProperties(InputStream inputStream, Integer appId, Integer envId) {
        if (inputStream == null) {
            return "流为空，无法解析";
        }
        Properties properties = PropUtils.loadProps(inputStream);
        if (properties == null) {
            return "读取的文件配置项为空";
        }
        List<ConfigDO> configDoList = convert2ConfigDO(properties, appId, envId);
        if (CollectionUtils.isEmpty(configDoList)) {
            return "配置文件为空";
        }
        configDoList.forEach(configDO -> {
            int count = configMapper.judgeKeyIsExist(configDO.getConfigKey(), appId, envId);
            if (count >= 1) {
                int updateRes = configMapper.updatePreConfigValue(configDO);
                if (updateRes != 1) {
                    throw new BusinessProcessFailException(BaseErrorEnum.UPDATE_ERROR);
                }
                return;
            }
            if (count == 0) {
                int insertRes = configMapper.insertSelective(configDO);
                if (insertRes != 1) {
                    throw new BusinessProcessFailException(BaseErrorEnum.SAVE_ERROR);
                }
            }
        });
        return null;
    }

    /**
     * 同步配置项至其他环境
     *
     * @param envId
     * @param appId
     * @param configKvVOList
     * @param userId
     */
    @Transactional(rollbackFor = BusinessProcessFailException.class)
    @Override
    public void syncConfigKey(Integer envId, Integer appId, List<ConfigKvVO> configKvVOList, Integer userId) {
        if (appId == null) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "请先选择系统");
        }
        if (envId == null) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "请先选择需要同步的环境");
        }
        if (CollectionUtils.isEmpty(configKvVOList)) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "同步的配置项不能为空");
        }
        configKvVOList.forEach(configKvVO -> {
            if (configKvVO == null) {
                return;
            }
            ConfigDO configInDb = configMapper.selectByAppIdAndEnvIdAndConfigKey(configKvVO.getKey(), appId, envId);
            if (configInDb == null) {
                // 保存key
                ConfigDO configDO = new ConfigDO();
                Date date = new Date();
                configDO.setGmtModified(date);
                configDO.setGmtCreate(date);
                configDO.setModifier(userId);
                configDO.setCreator(userId);
                configDO.setKeyVersion(DateUtils.getVersion());
                configDO.setKeyStatus(KeyStatusEnum.NOT_ENABLED.getKey());
                configDO.setIsDeleted(BaseBizEnum.YN_N.getCode());
                configDO.setConfigKey(configKvVO.getKey());
                configDO.setPreConfigValue(configKvVO.getValue());
                configDO.setAppId(appId);
                configDO.setEnvId(envId);
                configDO.setKeyDes("无");
                if (configMapper.insertSelective(configDO) != 1) {
                    throw new BusinessProcessFailException("同步配置项「" + configDO.getConfigKey()
                            + "」至【" + getEnv(envId) + "】环境失败！", BaseErrorEnum.SAVE_ERROR.getCode());
                }
            } else {
                ConfigDO configDO = new ConfigDO();
                configDO.setConfigKey(configKvVO.getKey());
                configDO.setPreConfigValue(configKvVO.getValue());
                configDO.setAppId(appId);
                configDO.setEnvId(envId);
                if (configMapper.updatePreConfigValue(configDO) != 1) {
                    throw new BusinessProcessFailException("同步配置项「" + configDO.getConfigKey()
                            + "」至【" + getEnv(envId) + "】环境失败！", BaseErrorEnum.SAVE_ERROR.getCode());
                }
            }
        });

    }


    //----------------------------------private method------------------------------------------


    /**
     * 转换
     *
     * @param properties
     * @param appId
     * @param envId
     * @return
     */
    private List<ConfigDO> convert2ConfigDO(Properties properties, Integer appId, Integer envId) {
        if (properties == null) {
            return null;
        }
        List<ConfigDO> configDoList = new ArrayList<>();
        Date date = new Date();
        properties.forEach((key, value) -> {
            if (StringUtils.isEmpty(key) || StringUtils.isEmpty(value)) {
                return;
            }
            ConfigDO configDO = new ConfigDO();
            configDO.setGmtCreate(date);
            configDO.setGmtModified(date);
            configDO.setCreator(0);
            configDO.setModifier(0);
            configDO.setIsDeleted(BaseBizEnum.YN_N.getCode());
            configDO.setKeyStatus(KeyStatusEnum.NOT_ENABLED.getKey());
            configDO.setKeyVersion(DateUtils.getVersion());
            configDO.setAppId(appId);
            configDO.setEnvId(envId);
            configDO.setKeyDes("无");
            configDO.setPreConfigValue(String.valueOf(value));
            configDO.setConfigKey(String.valueOf(key));
            configDoList.add(configDO);
        });
        return configDoList;
    }

    /**
     * 获取环境名称
     *
     * @param envId
     * @return
     */
    private String getEnv(Integer envId) {
        switch (envId) {
            case 0:
                return "dev";
            case 1:
                return "test";
            case 2:
                return "stable";
            case 3:
                return "online";
            default:
                throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR);
        }
    }

    /**
     * db对象转换
     *
     * @param configDoFromDb
     * @param operatorId
     * @return
     */
    private ConfigDO convertToConfigDoForUpdate(ConfigDO configDoFromDb, Integer operatorId) {
        ConfigDO configDO = new ConfigDO();
        BeanUtil.copyProperties(configDoFromDb, configDO);
        configDO.setKeyStatus(KeyStatusEnum.ENABLED.getKey());
        configDO.setModifier(operatorId);
        configDO.setPreConfigValue("");
        return configDO;
    }


    /**
     * 根据下发类型获取机器列表
     *
     * @param pushConfigKvVO
     * @param configKeyNodePath
     * @return
     */
    private List<String> getMachineListByPushType(PushConfigKvVO pushConfigKvVO, String configKeyNodePath) {
        List<String> machineList = null;
        // 灰度发布
        if (pushConfigKvVO.getPushType() == PushTypeEnum.GRAY_SCALE.getKey()) {
            if (CollectionUtils.isEmpty(pushConfigKvVO.getMachineList())) {
                throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "实例不能为空");
            }
            machineList = pushConfigKvVO.getMachineList();
        }
        // 全量发布
        if (pushConfigKvVO.getPushType() == PushTypeEnum.FULL.getKey()) {
            machineList = zookeeperComponent.getChildrenPath(configKeyNodePath);
        }
        return machineList;
    }

    /**
     * 参数校验
     *
     * @param pushConfigKvVO
     */
    @SuppressWarnings("Duplicates")
    private void checkPushConfigKeyVO(PushConfigKvVO pushConfigKvVO) {
        if (pushConfigKvVO == null) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "入参不能为空");
        }
        if (pushConfigKvVO.getAppId() == null) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "appId不能为空");
        }
        if (pushConfigKvVO.getId() == null) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "Id不能为空");
        }
        if (pushConfigKvVO.getPushType() == null || !EnumUtil.getKeys(PushTypeEnum.class).contains(pushConfigKvVO.getPushType())) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "下发类型不合法");
        }
    }

    /**
     * 在删除的时候记日志
     *
     * @param configDO
     * @param envId
     * @param operatorId
     */
    private void writeLogWhenDel(ConfigDO configDO, Integer envId, Integer operatorId, Integer operateType) {
        ConfigOperateLogDO configOperateLogDO = new ConfigOperateLogDO();
        this.fillBaseConfigOperateLogDO(configOperateLogDO, operatorId);
        configOperateLogDO.setAppId(configDO.getAppId());
        configOperateLogDO.setConfigKey(configDO.getConfigKey());
        configOperateLogDO.setEnvId(envId);
        configOperateLogDO.setOperateId(operatorId);
        configOperateLogDO.setOperateName(userAccountMapper.selectRealNameById(operatorId));
        configOperateLogDO.setOperateType(operateType);
        configOperateLogDO.setValueBefore(configDO.getConfigValue());
        configOperateLogDO.setVersionBefore(configDO.getKeyVersion());
        configOperateLogDO.setVersionAfter(null);
        configOperateLogDO.setValueAfter(null);
        configOperateLogDO.setKeyDes(configDO.getKeyDes());
        operateLogService.writeLog(configOperateLogDO);
    }


    /**
     * 填充基础数据
     *
     * @param configOperateLogDO
     * @param operatorId
     */
    private void fillBaseConfigOperateLogDO(ConfigOperateLogDO configOperateLogDO, Integer operatorId) {
        Date date = new Date();
        configOperateLogDO.setGmtCreate(date);
        configOperateLogDO.setGmtModified(date);
        configOperateLogDO.setCreator(operatorId);
        configOperateLogDO.setModifier(operatorId);
        configOperateLogDO.setIsDeleted(BaseBizEnum.YN_N.getCode());
    }

    /**
     * 参数校验
     *
     * @param isUpdate
     * @param configDataVO
     */
    private void checkConfigDataVO(ConfigDataVO configDataVO, boolean isUpdate) {
        if (configDataVO == null) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "入参不能为空");
        }
        if (configDataVO.getAppId() == null) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "appId不能为空");
        }
        if (StringUtils.isEmpty(configDataVO.getConfigKey())) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "key不能为空");
        }
        if (configDataVO.getConfigKey().length() > Constant.CONFIG_KEY_LENGTH) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "配置项的key不能超过1024个字符");
        }
        if (configDataVO.getConfigKey().contains(Constants.SLASH)) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "配置项的key不能包含 / ");
        }
        if (isUpdate) {
            if (StringUtils.isEmpty(configDataVO.getPreConfigValue())) {
                throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "预备值不能为空");
            }
        }
        if (StringUtils.isEmpty(configDataVO.getKeyDes())) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "键描述不能为空");
        }
        if (configDataVO.getKeyDes().length() > Constant.CONFIG_DES_LENGTH) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "配置项的描述不能超过1024个字符");
        }
        if (configDataVO.getEnvId() == null || EnvEnum.getEnvEnum(configDataVO.getEnvId()) == null) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "环境设置不合法");
        }
        if (!StringUtils.isEmpty(configDataVO.getKeyType())) {
            if (!EnumUtil.getNames(KeyTypeEnum.class).contains(configDataVO.getKeyType())) {
                throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "类型不合法");
            }
        }
    }


    /**
     * 组装对象
     *
     * @param configDO
     * @return
     */
    @SuppressWarnings("Duplicates")
    private ConfigKeyNodeData fillConfigKeyNodeData(ConfigDO configDO) {
        ConfigKeyNodeData configKeyNodeData = new ConfigKeyNodeData();
        configKeyNodeData.setKey(configDO.getConfigKey());
        configKeyNodeData.setValue(configDO.getConfigValue());
        configKeyNodeData.setVersion(configDO.getKeyVersion());
        return configKeyNodeData;
    }


    /**
     * 下发的时候记日志
     *
     * @param configDoForUpdate
     * @param configDoFromDb
     */
    private void writeLogWhenPush(ConfigDO configDoFromDb, ConfigDO configDoForUpdate) {
        ConfigOperateLogDO configOperateLogDO = new ConfigOperateLogDO();
        Integer operatorId = configDoForUpdate.getModifier();
        this.fillBaseConfigOperateLogDO(configOperateLogDO, operatorId);
        configOperateLogDO.setValueBefore(configDoFromDb.getConfigValue());
        configOperateLogDO.setVersionBefore(configDoFromDb.getKeyVersion());
        configOperateLogDO.setConfigKey(configDoFromDb.getConfigKey());
        configOperateLogDO.setVersionAfter(configDoForUpdate.getKeyVersion());
        configOperateLogDO.setAppId(configDoFromDb.getAppId());
        configOperateLogDO.setValueAfter(configDoForUpdate.getConfigValue());
        configOperateLogDO.setOperateId(operatorId);
        configOperateLogDO.setEnvId(configDoFromDb.getEnvId());
        configOperateLogDO.setOperateName(userAccountMapper.selectRealNameById(operatorId));
        configOperateLogDO.setOperateType(OperateTypeEnum.ADD.getKey());
        configOperateLogDO.setKeyDes(configDoFromDb.getKeyDes());
        operateLogService.writeLog(configOperateLogDO);
    }
}
