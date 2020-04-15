package com.bridge.domain;

/**
 * @author Jay
 * @version v1.0
 * @description 请添加类描述
 * @date 2019-01-02 15:59
 */
public final class Constants {

    /**
     * 系统管理员团队
     */
    public static final String TEAM_NAME = "系统管理员团队";

    /**
     * 斜杠
     */
    public final static String SLASH = "/";

    /**
     * 建立一个持久节点作为锁的root节点
     */
    public static final String ROOT = "/zk_bridge_root";

    /**
     * 开发环境
     */
    public static final String DEV_ROOT = "/zk_bridge_root/dev";

    /**
     * 测试环境
     */
    public static final String TEST_ROOT = "/zk_bridge_root/test";

    /**
     * 预发环境
     */
    public static final String STABLE_ROOT = "/zk_bridge_root/stable";

    /**
     * 线上环境
     */
    public static final String ONLINE_ROOT = "/zk_bridge_root/online";

    /**
     * 选举节点根目录
     */
    private static final String SELECTOR = ROOT.concat("/select_master");

    /**
     * job选举节点
     */
    public static final String JOB_MASTER = SELECTOR.concat("/job");

    /**
     * 请求成功
     */
    public static final int SUCCESS = 800;

    /**
     * log格式
     */
    public static final String PRINT_LOG_FORMAT = "Loading bean 「{}」's property -> 「{}」, key -> 「{}」, value -> 「{}」";
//    public static final String PRINT_LOG_FORMAT = "正在加载 -> 「{}」 的成员变量 -> 「{}」, 配置项为  -> 「{}」, 值为  ->  「{}」";

    /**
     * 控制台的appCode
     */
    public static final String BRIDGE_CONSOLE_APP_CODE = "0000-0000-0000-0000";

    /**
     * 控制台名称
     */
    public static final String BRIDGE_CONSOLE_APP_NAME = "控制台";


    //----------------------推送至控制台的log---------------

    public static final String LOAD_FORMAT = "正在加载 -> 「%s」 的成员变量 -> 「%s」, 配置项为  -> 「%s」, 值为  ->  「%s」";


    /**
     * 全局的配置文件的bean名称
     */
    public static final String BRIDGE_CONFIG_PROPERTIES_BEAN_NAME = "bridgeConfigProperties";


}
