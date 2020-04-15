package com.bridge.console.service.system.impl;

import com.bridge.console.model.constant.Constant;
import com.bridge.console.model.dao.AppDefMapper;
import com.bridge.console.model.dao.ConfigMapper;
import com.bridge.console.model.dao.UserAccountMapper;
import com.bridge.console.model.entity.AppDefDO;
import com.bridge.console.model.entity.UserAccountDO;
import com.bridge.console.model.vo.ValueVO;
import com.bridge.console.service.zk.ZookeeperComponent;
import com.bridge.enums.EnabledStateEnum;
import com.bridge.console.model.vo.AppDefEditOrAddVO;
import com.bridge.console.service.system.AppManagerService;
import com.bridge.console.utils.BeanUtil;
import com.bridge.console.utils.EncryptUtil;
import com.bridge.console.utils.ex.BusinessCheckFailException;
import com.bridge.console.utils.result.BaseBizEnum;
import com.bridge.console.utils.result.BaseErrorEnum;
import com.bridge.enums.EnvEnum;
import com.bridge.utils.NodePathUtils;
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
 * @description 应用管理的服务
 * @date 2019-01-25 15:44
 */
@Service
@Slf4j
public class AppManagerServiceImpl implements AppManagerService {

    @Autowired
    private AppDefMapper appDefMapper;

    @Autowired
    private UserAccountMapper userAccountMapper;

    @Autowired
    private ConfigMapper configMapper;

    @Autowired
    private ZookeeperComponent zookeeperComponent;

    /**
     * 应用修改
     *
     * @param appDefEditOrAddVO
     * @param modifier
     * @return
     */
    @Override
    @SuppressWarnings("Duplicates")
    public Boolean editApp(AppDefEditOrAddVO appDefEditOrAddVO, Integer modifier) {
        if (appDefEditOrAddVO != null && !StringUtils.isEmpty(appDefEditOrAddVO.getAppDes())) {
            if (appDefEditOrAddVO.getAppDes().length() > Constant.STRING_LENGTH) {
                throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "系统描述过长,不能超过200个字符");
            }
        }
        if (appDefEditOrAddVO != null && !StringUtils.isEmpty(appDefEditOrAddVO.getAppName())) {
            if (appDefEditOrAddVO.getAppDes().length() > Constant.STRING_LENGTH) {
                throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "系统名称过长,不能超过200个字符");
            }
        }
        AppDefDO appDefDO = this.getAppDefDO(appDefEditOrAddVO, true);
        appDefDO.setModifier(modifier);
        if (appDefMapper.updateById(appDefDO) != BaseBizEnum.FIRST.getCode()) {
            throw new BusinessCheckFailException(BaseErrorEnum.UPDATE_ERROR.getCode(), "更新失败");
        }
        return Boolean.TRUE;
    }

    /**
     * 应用新增
     *
     * @param operateId
     * @param appDefEditOrAddVO
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    @SuppressWarnings("Duplicates")
    public Boolean addApp(AppDefEditOrAddVO appDefEditOrAddVO, Integer operateId) {
        if (appDefEditOrAddVO != null && !StringUtils.isEmpty(appDefEditOrAddVO.getAppDes())) {
            if (appDefEditOrAddVO.getAppDes().length() > Constant.STRING_LENGTH) {
                throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "系统描述过长,不能超过200个字符");
            }
        }
        if (appDefEditOrAddVO != null && !StringUtils.isEmpty(appDefEditOrAddVO.getAppName())) {
            if (appDefEditOrAddVO.getAppDes().length() > Constant.STRING_LENGTH) {
                throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "系统名称过长,不能超过200个字符");
            }
        }
        if (appDefEditOrAddVO == null || StringUtils.isEmpty(appDefEditOrAddVO.getAppName())) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "appName不能为空");
        }
        AppDefDO appDefDoFromDb = appDefMapper.selectByAppName(appDefEditOrAddVO.getAppName());
        if (appDefDoFromDb != null) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "系统名称已存在，请重新选取！");
        }
        AppDefDO appDefDO = this.getAppDefDO(appDefEditOrAddVO, false);
        Date date = new Date();
        appDefDO.setModifier(operateId);
        appDefDO.setCreator(operateId);
        appDefDO.setGmtCreate(date);
        appDefDO.setGmtModified(date);
        appDefDO.setIsDeleted(BaseBizEnum.YN_N.getCode());
        appDefDO.setEnabledState(EnabledStateEnum.ENABLED.getKey());
        appDefDO.setAppCode(EncryptUtil.getAppCode());
        if (appDefMapper.insert(appDefDO) != BaseBizEnum.FIRST.getCode()) {
            throw new BusinessCheckFailException(BaseErrorEnum.UPDATE_ERROR.getCode(), "保存数据失败");
        }
        // 创建zk节点
        zookeeperComponent.createAppNodeWithEnv(appDefDO);
        return Boolean.TRUE;
    }

    /**
     * 删除应用
     *
     * @param id
     * @param operateId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean deleteApp(Integer id, Integer operateId) {
        if (id == null) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "id不能为空");
        }
        AppDefDO appDefDO = appDefMapper.selectByPrimaryKey(id);
        if (appDefDO == null) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "记录不存在");
        }
        // 判断该应用下是否还有配置项,有的话就不能删除
        List<ValueVO> valueVoList = configMapper.selectConfigKeyList(id, null);
        if (!CollectionUtils.isEmpty(valueVoList)) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode()
                    , "该应用下存在配置项，若要删除，请先删除该应用下所有环境的的配置项!");
        }
        // 删除db数据
        if (appDefMapper.deleteById(id, operateId) != 1) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "删除记录失败");
        }
        // 删除zk数据，这里删除的是dev test stable online环境的app
        for (EnvEnum envEnum : EnvEnum.values()) {
            String appNodePath = NodePathUtils.getAppNodePath(appDefDO.getAppCode(), envEnum);
            zookeeperComponent.deletingChildrenIfNeeded(appNodePath);
        }
        return Boolean.TRUE;
    }

    //-----------------------------------------------------private method------------------------------------------------------


    /**
     * 更新/新增前的动作
     *
     * @param isEdit
     * @param appDefEditOrAddVO
     * @return
     */
    private AppDefDO getAppDefDO(AppDefEditOrAddVO appDefEditOrAddVO, boolean isEdit) {
        // 参数校验
        this.checkAppDefEditVO(appDefEditOrAddVO, isEdit);
        // 判断人与团队是否匹配
        this.judgeOwnerIdIsBelongsToThatTeam(appDefEditOrAddVO.getAppOwner(), appDefEditOrAddVO.getTeamId());
        // 插入记录
        AppDefDO appDefDO = new AppDefDO();
        BeanUtil.copyProperties(appDefEditOrAddVO, appDefDO);
        return appDefDO;
    }


    /**
     * 判断人与团队是否匹配
     *
     * @param appOwner
     * @param teamId
     */
    private void judgeOwnerIdIsBelongsToThatTeam(Integer appOwner, Integer teamId) {
        UserAccountDO userAccountDO = userAccountMapper.selectByPrimaryKey(appOwner);
        if (userAccountDO == null) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "该系统负责人不存在");
        }
        if (userAccountDO.getTeamId() == null || !userAccountDO.getTeamId().equals(teamId)) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "该系统负责人与团队不匹配");
        }
    }

    /**
     * 参数校验
     *
     * @param isEdit
     * @param appDefEditOrAddVO
     */
    private void checkAppDefEditVO(AppDefEditOrAddVO appDefEditOrAddVO, boolean isEdit) {
        if (appDefEditOrAddVO == null) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "编辑参数不能为空");
        }
        // 如果是更新操作需要校验id
        if (isEdit) {
            if (appDefEditOrAddVO.getId() == null) {
                throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "id不能为空");
            }
        }
        if (StringUtils.isEmpty(appDefEditOrAddVO.getAppDes())) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "系统描述不能为空");
        }
        if (StringUtils.isEmpty(appDefEditOrAddVO.getAppName())) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "系统名称不能为空");
        }
        if (StringUtils.isEmpty(appDefEditOrAddVO.getAppOwner())) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "系统负责人不能为空");
        }
        if (StringUtils.isEmpty(appDefEditOrAddVO.getTeamId())) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "系统所属团队不能为空");
        }
    }
}
