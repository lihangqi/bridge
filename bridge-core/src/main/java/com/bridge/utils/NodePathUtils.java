package com.bridge.utils;

import com.bridge.domain.Constants;
import com.bridge.exception.BridgeProcessFailException;
import com.bridge.enums.BridgeErrorEnum;
import com.bridge.enums.EnvEnum;
import org.springframework.util.StringUtils;

/**
 * @author Jay
 * @version v1.0
 * @description 请添加类描述
 * @date 2019-02-13 15:25
 */
public class NodePathUtils {


    private NodePathUtils() {

    }

    private static String IP = null;

    private static final String CONSUMER_PATH = "_consumer_host";

    /**
     * 根据环境取根节点
     *
     * @param envEnum
     * @return
     */
    private static String getRootPathByEnv(EnvEnum envEnum) {
        switch (envEnum) {
            case DEV:
                return Constants.DEV_ROOT;
            case TEST:
                return Constants.TEST_ROOT;
            case STABLE:
                return Constants.STABLE_ROOT;
            case ONLINE:
                return Constants.ONLINE_ROOT;
            default:
                throw new BridgeProcessFailException(BridgeErrorEnum.BNS_CHK_ERROR.getCode(), "环境设置错误");
        }
    }

    /**
     * 拼接app的zk节点路径
     *
     * @param appCode
     * @return
     */
    public static String getAppNodePath(String appCode, EnvEnum envEnum) {
        return getRootPathByEnv(envEnum).concat(Constants.SLASH).concat(appCode);
    }


    /**
     * 拼接app的configKey的节点路径
     *
     * @param appCode
     * @return
     */
    public static String getConfigKeyNodePath(String appCode, String configKey, EnvEnum envEnum) {
        return getAppNodePath(appCode, envEnum).concat(Constants.SLASH).concat(configKey);
    }


    /**
     * 拼接app的configKey的节点路径
     *
     * @param appNodePath
     * @param configKey
     * @return
     */
    public static String getConfigKeyNodePathByAppNodePathAndConfigKey(String appNodePath, String configKey) {
        return appNodePath.concat(Constants.SLASH).concat(configKey);
    }


    /**
     * machine节点
     *
     * @param appCode
     * @param configKey
     * @param host
     * @return
     */
    public static String getMachineNodePath(String appCode, String configKey, String host, EnvEnum envEnum) {
        return getConfigKeyNodePath(appCode, configKey, envEnum).concat(Constants.SLASH).concat(host);
    }


    /**
     * machine节点
     *
     * @param configKeyNodePath configKey的节点路径
     * @param host
     * @return
     */
    public static String getMachineNodePath(String configKeyNodePath, String host) {
        return configKeyNodePath.concat(Constants.SLASH).concat(host);
    }


    /**
     * 消费者的ip节点
     *
     * @param appCode
     * @param envEnum
     * @param ip
     * @return
     */
    public static String getConsumerHostPath(String appCode, EnvEnum envEnum, String ip) {
        return getAppNodePath(appCode, envEnum)
                .concat(Constants.SLASH)
                .concat(appCode)
                .concat(CONSUMER_PATH)
                .concat(Constants.SLASH)
                .concat(ip);
    }


    /**
     * 拼接消费者的节点路径
     *
     * @param appCode
     * @param envEnum
     * @return
     */
    public static String getConsumerHostNode(String appCode, EnvEnum envEnum) {
        return getAppNodePath(appCode, envEnum)
                .concat(Constants.SLASH)
                .concat(appCode)
                .concat(CONSUMER_PATH);
    }

    /**
     * 拼接消费者key
     *
     * @param appCode
     * @return
     */
    public static String getConsumerHostKey(String appCode){
        return appCode.concat(CONSUMER_PATH);
    }

    /**
     * 获取ip
     *
     * @return
     */
    public static String getIp() {
        if (IP == null) {
            IP = getHostIp();
            if (StringUtils.isEmpty(IP)) {
                throw new BridgeProcessFailException(BridgeErrorEnum.UNKNOWN_ERROR.getCode(), "获取ip出错");
            }
        }
        return IP;
    }


    /**
     * 获取本机ip
     *
     * @return
     */
    private static String getHostIp() {
        try {
            return NetUtils.getIp();
        } catch (Exception e) {
            throw new BridgeProcessFailException(BridgeErrorEnum.BNS_CHK_ERROR.getCode(), e.getMessage());
        }
    }

}
