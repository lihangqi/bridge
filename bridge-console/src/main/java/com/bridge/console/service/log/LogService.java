package com.bridge.console.service.log;

import com.bridge.console.model.entity.SystemLogDO;
import com.bridge.console.model.vo.SystemLogQuery;
import com.bridge.domain.SystemLogDTO;
import com.bridge.enums.LogLevelEnum;

import java.util.List;

/**
 * @author Jay
 * @version v1.0
 * @description 请添加类描述
 * @date 2019-08-30 17:48
 */
public interface LogService {

    /**
     * 处理客户端发送过来的log日志
     *
     * @param systemLogDTO
     * @return
     */
    Boolean handleLog(SystemLogDTO systemLogDTO);


    /**
     * 记录控制台的日志
     *
     * @param logContent
     * @param logLevelEnum
     */
    void pushConsoleLog(String logContent, LogLevelEnum logLevelEnum);


    /**
     * 查询记录条数
     *
     * @param systemLogQuery
     * @return
     */
    int countSystemLogList(SystemLogQuery systemLogQuery);


    /**
     * 查询日志记录
     *
     * @param systemLogQuery
     * @return
     */
    List<SystemLogDO> querySystemLogList(SystemLogQuery systemLogQuery);
}
