package com.bridge.console.web;

import com.bridge.console.annotation.NotCertification;
import com.bridge.console.model.entity.SystemLogDO;
import com.bridge.console.model.vo.SystemLogQuery;
import com.bridge.console.model.vo.SystemLogVO;
import com.bridge.console.service.log.LogService;
import com.bridge.console.utils.ex.BusinessCheckFailException;
import com.bridge.console.utils.result.*;
import com.bridge.domain.SystemLogDTO;
import com.bridge.enums.EnvEnum;
import com.bridge.enums.LogLevelEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import static com.bridge.domain.Constants.BRIDGE_CONSOLE_APP_CODE;
import static com.bridge.domain.Constants.BRIDGE_CONSOLE_APP_NAME;


/**
 * @author Jay
 * @version v1.0
 * @description 处理客户端发送的日志
 * @date 2019-08-30 11:09
 */
@RestController
public class LogController extends BaseController {

    @Autowired
    private LogService logService;


    /**
     * 推送log接口
     *
     * @param systemLogDTO
     * @return
     */
    @NotCertification
    @ResponseBody
    @RequestMapping("/pushLog")
    public Result<Boolean> pushLog(SystemLogDTO systemLogDTO) {
        return Result.wrapSuccessfulResult(logService.handleLog(systemLogDTO));
    }


    /**
     * 分页查询日志接口
     *
     * @param systemLogQuery
     * @param pageable
     * @return
     */
    @ResponseBody
    @RequestMapping("/querySystemLog")
    @SuppressWarnings("Duplicates")
    public PagingResult<SystemLogVO> querySystemLog(SystemLogQuery systemLogQuery
            , @PageableDefault(page = 1, size = 20, sort = "log_record_time", direction = Sort.Direction.DESC) Pageable pageable) {
        if (systemLogQuery == null || systemLogQuery.getEnvId() == null) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "envId不能为空");
        }
        if (CollectionUtils.isEmpty(systemLogQuery.getAppCodeList())) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "appCodeList不能为空");
        }
        int total = logService.countSystemLogList(systemLogQuery);
        if (total == BaseBizEnum.ZERO.getCode()) {
            return PagingResult.wrapSuccessfulResult(new ArrayList<>(), pageable, total);
        }
        PageUtil.fillParam(systemLogQuery, pageable);
        List<SystemLogDO> systemLogList = logService.querySystemLogList(systemLogQuery);
        if (CollectionUtils.isEmpty(systemLogList)) {
            return PagingResult.wrapSuccessfulResult(new ArrayList<>(), pageable, total);
        }
        return PagingResult.wrapSuccessfulResult(convertToSystemLogList(systemLogList), pageable, total);
    }


    //----------------------------------------------------private method---------------------------------------------------

    /**
     * VO转换
     *
     * @param systemLogList
     * @return
     */
    private List<SystemLogVO> convertToSystemLogList(List<SystemLogDO> systemLogList) {
        List<SystemLogVO> systemLogVoList = new ArrayList<>();
        systemLogList.forEach(systemLogDO -> {
                    SystemLogVO systemLogVO = new SystemLogVO();
                    if (!StringUtils.isEmpty(systemLogDO.getLogRecordTime())) {
                        systemLogVO.setLogRecordTime(systemLogDO.getLogRecordTime().substring(0, systemLogDO.getLogRecordTime().length() - 6));
                    }
                    systemLogVO.setIp(systemLogDO.getClientIp());
                    systemLogVO.setLogLevel(systemLogDO.getLogLevel());
                    LogLevelEnum logLevelEnum = LogLevelEnum.getLogLevelEnum(systemLogDO.getLogLevel());
                    if (logLevelEnum != null) {
                        systemLogVO.setLogLevelStr(logLevelEnum.getValue());
                    }
                    if (!StringUtils.isEmpty(systemLogDO.getAppCode())) {
                        if (BRIDGE_CONSOLE_APP_CODE.equals(systemLogDO.getAppCode())) {
                            systemLogVO.setAppName(BRIDGE_CONSOLE_APP_NAME);
                        } else {
                            systemLogVO.setAppName(systemLogDO.getAppName());
                        }
                    }
                    systemLogVO.setEnvId(systemLogDO.getEnvId());
                    systemLogVO.setLogContent(systemLogDO.getLogContent());
                    systemLogVoList.add(systemLogVO);
                }
        );
        return systemLogVoList;
    }
}
