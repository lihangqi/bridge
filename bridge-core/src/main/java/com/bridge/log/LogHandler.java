package com.bridge.log;

import com.bridge.domain.BridgeConfig;
import com.bridge.domain.SystemLogDTO;
import com.bridge.enums.LogLevelEnum;
import com.bridge.utils.DateUtils;
import com.bridge.utils.NodePathUtils;
import com.bridge.utils.rpc.RpcServiceHandler;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.concurrent.*;

/**
 * @author Jay
 * @version v1.0
 * @description 异步发送本地日志
 * @date 2019-09-03 11:26
 */
@Slf4j
public class LogHandler {

    private static BridgeConfig bridgeConfig;


    private static ThreadFactory threadFactory = new ThreadFactoryBuilder()
            .setNameFormat("bridge-pool-pushLog-%d").setDaemon(true).build();

    private static ExecutorService executorService = new ThreadPoolExecutor(5, 10,
            60L, TimeUnit.SECONDS, new LinkedBlockingDeque<>(1024), threadFactory,
            new ThreadPoolExecutor.AbortPolicy());

    private LogHandler() {

    }


    public static void init(BridgeConfig bridgeConfigObj) {
        bridgeConfig = bridgeConfigObj;
    }

    /**
     * 发送本地日志
     *
     * @param logContent
     * @param logLevelEnum
     */
    private static void pushLog(String logContent, LogLevelEnum logLevelEnum) {
        SystemLogDTO systemLogDTO = new SystemLogDTO();
        systemLogDTO.setLogContent(logContent);
        systemLogDTO.setLogLevel(logLevelEnum.getKey());
        systemLogDTO.setAppCode(bridgeConfig.getAppCode());
        systemLogDTO.setEnvId(bridgeConfig.getEnvEnum().getEnvId());
        systemLogDTO.setIp(NodePathUtils.getIp());
        systemLogDTO.setLogRecordTime(DateUtils.format(new Date(), null));
        executorService.execute(() -> RpcServiceHandler.pushLog(bridgeConfig.getServerUrl(), systemLogDTO));
    }


    public static void info(String logContent) {
        pushLog(logContent, LogLevelEnum.INFO);
    }

    public static void warn(String logContent) {
        pushLog(logContent, LogLevelEnum.WARN);
    }

    public static void error(String logContent) {
        pushLog(logContent, LogLevelEnum.ERROR);
    }

    public static void debug(String logContent) {
        pushLog(logContent, LogLevelEnum.DEBUG);
    }


}
