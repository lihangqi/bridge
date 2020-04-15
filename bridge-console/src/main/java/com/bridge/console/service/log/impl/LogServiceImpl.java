package com.bridge.console.service.log.impl;

import com.bridge.console.model.dao.AppDefMapper;
import com.bridge.console.model.dao.SystemLogMapper;
import com.bridge.console.model.entity.SystemLogDO;
import com.bridge.console.model.vo.SystemLogQuery;
import com.bridge.console.model.vo.SystemLogVO;
import com.bridge.console.service.log.LogService;
import com.bridge.console.utils.BeanUtil;
import com.bridge.console.utils.ex.BusinessCheckFailException;
import com.bridge.console.utils.result.BaseBizEnum;
import com.bridge.console.utils.result.BaseErrorEnum;
import com.bridge.console.websocket.WebSocketServer;
import com.bridge.domain.SystemLogDTO;
import com.bridge.enums.EnvEnum;
import com.bridge.enums.LogLevelEnum;
import com.bridge.utils.DateUtils;
import com.bridge.utils.NodePathUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

import static com.bridge.domain.Constants.BRIDGE_CONSOLE_APP_CODE;
import static com.bridge.domain.Constants.BRIDGE_CONSOLE_APP_NAME;

/**
 * @author Jay
 * @version v1.0
 * @description 请添加类描述
 * @date 2019-08-30 17:48
 */
@Slf4j
@Service
public class LogServiceImpl implements LogService {

    @Autowired
    private SystemLogMapper systemLogMapper;

    @Autowired
    private AppDefMapper appDefMapper;

    /**
     * 处理客户端发送过来的log日志
     *
     * @param systemLogDTO
     * @return
     */
    @Override
    public Boolean handleLog(SystemLogDTO systemLogDTO) {
        // 参数校验
        this.checkParam(systemLogDTO);
        // 保存到db
        SystemLogDO systemLogDO = new SystemLogDO();
        systemLogDO.setIsDeleted(BaseBizEnum.YN_N.getCode());
        systemLogDO.setClientIp(systemLogDTO.getIp());
        systemLogDO.setAppName(appDefMapper.selectAppNameByAppCode(systemLogDTO.getAppCode()));
        BeanUtil.copyProperties(systemLogDTO, systemLogDO);
        if (systemLogMapper.insertSelective(systemLogDO) != BaseBizEnum.FIRST.getCode()) {
            throw new BusinessCheckFailException(BaseErrorEnum.SAVE_ERROR.getCode(), "保存日志失败");
        }
        // websocket推送
        SystemLogVO systemLogVO = new SystemLogVO();
        BeanUtil.copyProperties(systemLogDTO, systemLogVO);
        systemLogVO.setAppName(appDefMapper.selectAppNameByAppCode(systemLogDTO.getAppCode()));
        LogLevelEnum logLevelEnum = LogLevelEnum.getLogLevelEnum(systemLogDTO.getLogLevel());
        if (logLevelEnum != null) {
            systemLogVO.setLogLevelStr(logLevelEnum.getValue());
        }
        systemLogVO.setAppCode(systemLogDTO.getAppCode());
        // 只推送指定的项目
        WebSocketServer.sendLog(systemLogVO);
        return Boolean.TRUE;
    }

    /**
     * 记录控制台的日志
     */
    @Override
    public void pushConsoleLog(String logContent, LogLevelEnum logLevelEnum) {
        try {
            if (StringUtils.isEmpty(logContent) || logLevelEnum == null) {
                log.error("日志参数不能为空");
                return;
            }
            SystemLogDO systemLogDO = new SystemLogDO();
            systemLogDO.setIsDeleted(BaseBizEnum.YN_N.getCode());
            systemLogDO.setEnvId(BaseBizEnum.FOUR.getCode());
            systemLogDO.setAppCode(BRIDGE_CONSOLE_APP_CODE);
            systemLogDO.setClientIp(NodePathUtils.getIp());
            systemLogDO.setAppName(BRIDGE_CONSOLE_APP_NAME);
            systemLogDO.setLogLevel(logLevelEnum.getKey());
            systemLogDO.setLogRecordTime(DateUtils.format(new Date(), null));
            systemLogDO.setLogContent(logContent);
            if (systemLogMapper.insertSelective(systemLogDO) != BaseBizEnum.FIRST.getCode()) {
                log.error("保存日志失败，入参为：{}", systemLogDO);
                return;
            }
            // websocket推送
            SystemLogVO systemLogVO = new SystemLogVO();
            BeanUtil.copyProperties(systemLogDO, systemLogVO);
            systemLogVO.setLogLevelStr(logLevelEnum.getValue());
            systemLogVO.setIp(systemLogDO.getClientIp());
            systemLogVO.setAppCode(BRIDGE_CONSOLE_APP_CODE);
            WebSocketServer.sendLog(systemLogVO);
        } catch (Exception e) {
            log.error("保存日志失败，异常信息为：", e);
        }
    }

    /**
     * 查询记录条数
     *
     * @param systemLogQuery
     * @return
     */
    @Override
    public int countSystemLogList(SystemLogQuery systemLogQuery) {
        return systemLogMapper.countSystemLogList(systemLogQuery);
    }

    /**
     * 查询日志记录
     *
     * @param systemLogQuery
     * @return
     */
    @Override
    public List<SystemLogDO> querySystemLogList(SystemLogQuery systemLogQuery) {
        return systemLogMapper.querySystemLogList(systemLogQuery);
    }


    /*-------------------------------------------private method------------------------------------------*/

    /**
     * 参数校验
     *
     * @param systemLogDTO
     */
    private void checkParam(SystemLogDTO systemLogDTO) {
        if (systemLogDTO == null) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "入参不能为空");
        }
        if (StringUtils.isEmpty(systemLogDTO.getIp())) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "ip不能为空");
        }
        if (StringUtils.isEmpty(systemLogDTO.getAppCode())) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "appCode不能为空");
        }
        if (StringUtils.isEmpty(systemLogDTO.getLogContent())) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "logContent不能为空");
        }
        if (systemLogDTO.getLogRecordTime() == null) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "logRecordTime不能为空");
        }
        if (systemLogDTO.getEnvId() == null) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "envId不能为空");
        }
        if (EnvEnum.getEnvEnum(systemLogDTO.getEnvId()) == null) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "envId不合法");
        }
        if (systemLogDTO.getLogLevel() == null) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "logLevel不能为空");
        }
        if (LogLevelEnum.getLogLevelEnum(systemLogDTO.getLogLevel()) == null) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "logLevel不合法");
        }
    }
}
