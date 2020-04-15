package com.bridge.console.web;

import com.bridge.console.model.dao.AppDefMapper;
import com.bridge.console.model.dao.ConfigOperateLogMapper;
import com.bridge.console.model.dao.UserAccountMapper;
import com.bridge.console.model.entity.ConfigOperateLogDO;
import com.bridge.console.model.enums.OperateTypeEnum;
import com.bridge.console.model.vo.OperateLogQuery;
import com.bridge.console.model.vo.OperateLogVO;
import com.bridge.console.service.operatelog.OperateLogService;
import com.bridge.console.utils.BeanUtil;
import com.bridge.console.utils.EnumUtil;
import com.bridge.console.utils.ex.BusinessCheckFailException;
import com.bridge.console.utils.result.BaseErrorEnum;
import com.bridge.console.utils.result.PageUtil;
import com.bridge.console.utils.result.PagingResult;
import com.bridge.console.utils.result.Result;
import com.bridge.enums.EnvEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jay
 * @version v1.0
 * @description 操作日志相关
 * @date 2019-02-20 14:54
 */
@RestController
public class OperateLogController extends BaseController {


    @Autowired
    private OperateLogService operateLogService;

    @Autowired
    private ConfigOperateLogMapper configOperateLogMapper;

    @Autowired
    private UserAccountMapper userAccountMapper;

    @Autowired
    private AppDefMapper appDefMapper;

    /**
     * 查询操作历史记录
     *
     * @return
     */
    @RequestMapping("/queryOperateLogList")
    @ResponseBody
    @SuppressWarnings("Duplicates")
    public PagingResult<OperateLogVO> queryOperateLogList(OperateLogQuery operateLogQuery
            , @PageableDefault(page = 1, size = 20, sort = "gmt_create", direction = Sort.Direction.DESC) Pageable pageable) {
        if (operateLogQuery == null || operateLogQuery.getEnvId() == null) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "envId不能为空");
        }
        if (EnvEnum.getEnvEnum(operateLogQuery.getEnvId()) == null) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "envId传入值不合法");
        }
        Integer teamId = getSessionContext().getTeamId();
        // 管理员查询所有
        if (isAdmin()) {
            teamId = null;
            operateLogQuery.setAppIdList(null);
        }
        // 模糊查询出appId的集合
        List<Integer> appIdList = appDefMapper.queryAppIdList(operateLogQuery.getAppName(), teamId);
        // 团队leader和普通用户查询团队下的项目
        operateLogQuery.setAppIdList(appIdList);
        int total = configOperateLogMapper.countOperateLogList(operateLogQuery);
        if (total == 0) {
            return PagingResult.wrapSuccessfulResult(new ArrayList<>(), pageable, total);
        }
        PageUtil.fillParam(operateLogQuery, pageable);
        List<ConfigOperateLogDO> configOperateLogDoList = configOperateLogMapper.queryOperateLogList(operateLogQuery);
        if (CollectionUtils.isEmpty(configOperateLogDoList)) {
            return PagingResult.wrapSuccessfulResult(new ArrayList<>(), pageable, total);
        }
        List<OperateLogVO> operateLogVoList = BeanUtil.copyList(configOperateLogDoList, OperateLogVO.class);
        operateLogVoList.forEach(operateLogVO -> {
            operateLogVO.setOperateTypeStr(EnumUtil.getName(operateLogVO.getOperateType(), OperateTypeEnum.class));
            operateLogVO.setAppName(appDefMapper.selectAppNameById(operateLogVO.getAppId()));
            operateLogVO.setOperateName(userAccountMapper.selectRealNameById(operateLogVO.getOperateId()));
        });
        return PagingResult.wrapSuccessfulResult(operateLogVoList, pageable, total);
    }


    /**
     * 回滚数据
     *
     * @param id
     * @return
     */
    @RequestMapping("/rollbackData")
    @ResponseBody
    public Result<Boolean> rollbackData(Integer id) {
        Integer roleType = getSessionContext().getAccountRole();
        Integer teamId = getSessionContext().getTeamId();
        Integer accountId = getSessionContext().getId();
        return Result.wrapSuccessfulResult(operateLogService.rollbackData(id, accountId, roleType, teamId));
    }


}
