package com.bridge.utils;

import com.bridge.enums.BridgeErrorEnum;
import com.bridge.exception.BridgeProcessFailException;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.regex.Pattern;

/**
 * @author Jay
 * @version v1.0
 * @description ip相关的
 * @date 2019-09-09 16:47
 */
@Slf4j
public class NetUtils {

    private NetUtils() {

    }

    private static final String LOCALHOST = "127.0.0.1";

    private static final String ANY_HOST = "0.0.0.0";

    private static final Pattern IP_PATTERN = Pattern.compile("\\d{1,3}(\\.\\d{1,3}){3,5}$");

    private static volatile InetAddress LOCAL_ADDRESS = null;


    /**
     * 获取ip
     *
     * @return
     */
    public static String getIp() {
        if (LOCAL_ADDRESS != null) {
            return LOCAL_ADDRESS.getHostAddress();
        }
        return getLocalAddress().getHostAddress();
    }


    /**
     * 遍历本地网卡，返回第一个合理的IP。
     *
     * @return 本地网卡IP
     */
    private static InetAddress getLocalAddress() {
        if (LOCAL_ADDRESS != null) {
            return LOCAL_ADDRESS;
        }
        InetAddress localAddress = getLocalAdd();
        LOCAL_ADDRESS = localAddress;
        return localAddress;
    }


    /**
     * 获取InetAddress
     *
     * @return
     */
    private static InetAddress getLocalAdd() {
        try {
            InetAddress localAddress = InetAddress.getLocalHost();
            if (isValidAddress(localAddress)) {
                return localAddress;
            }
        } catch (Throwable e) {
            log.error("获取本机ip失败: ", e);
            throw new BridgeProcessFailException(BridgeErrorEnum.UNKNOWN_ERROR.getCode(), "获取本机ip失败");
        }
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            if (interfaces != null) {
                while (interfaces.hasMoreElements()) {
                    try {
                        NetworkInterface network = interfaces.nextElement();
                        Enumeration<InetAddress> addresses = network.getInetAddresses();
                        while (addresses.hasMoreElements()) {
                            try {
                                InetAddress address = addresses.nextElement();
                                if (isValidAddress(address)) {
                                    return address;
                                }
                            } catch (Throwable e) {
                                log.error("获取本机ip失败: ", e);
                                throw new BridgeProcessFailException(BridgeErrorEnum.UNKNOWN_ERROR.getCode(), "获取本机ip失败");
                            }
                        }
                    } catch (Throwable e) {
                        log.error("获取本机ip失败: ", e);
                        throw new BridgeProcessFailException(BridgeErrorEnum.UNKNOWN_ERROR.getCode(), "获取本机ip失败");
                    }
                }
            }
        } catch (Throwable e) {
            log.error("获取本机ip失败: ", e);
            throw new BridgeProcessFailException(BridgeErrorEnum.UNKNOWN_ERROR.getCode(), "获取本机ip失败");
        }
        throw new BridgeProcessFailException(BridgeErrorEnum.UNKNOWN_ERROR.getCode(), "获取本机ip失败");
    }


    /**
     * 校验
     *
     * @param address
     * @return
     */
    private static boolean isValidAddress(InetAddress address) {
        if (address == null || address.isLoopbackAddress()) {
            return false;
        }
        String name = address.getHostAddress();
        return (name != null && !ANY_HOST.equals(name) && !LOCALHOST.equals(name) && IP_PATTERN.matcher(name).matches());
    }

}
