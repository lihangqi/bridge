package com.bridge.enums;


/**
 * @description: 相关枚举
 * @author: Jay
 * @date: 2018-3-28 14:35:30
 * @version: V1.0
 */
public enum BridgeErrorEnum implements BaseError {


    // 业务校验异常
    BNS_CHK_ERROR(-1, "参数校验异常"),
    NOT_INSTANTIATION_ERROR(-2, "该类不能被实例化"),
    CONVERT_ERROR(-3, "类型转换异常"),
    UNKNOWN_ERROR(-4, "未知异常"),
    ZK_NULL_ERROR(-5, "zk返回结果集出现空值"),
    ZK_NODE_CREATE_ERROR(-6, "zk节点创建失败"),
    ZK_NODE_DELETED_ERROR(-7, "zk节点删除失败"),
    ZK_NODE_READ_ERROR(-7, "zk节点读取内容失败"),
    ZK_NODE_UPDATE_ERROR(-8, "zk节点数据更新失败"),
    ZK_NODE_WATCHER_ERROR(-9, "添加zk节点监听失败"),
    ZK_NODE_CHECK_ERROR(-10, "zk节点检查失败"),
    RPC_ERROR(-11, "远程调用异常"),
    CACHE_REFRESH_ERROR(-12, "缓存刷新失败"),
    CACHE_KEY_ERROR(-13, "缓存数据异常"),
    XML_PARES_ERROR(-14, "xml占位符解析异常"),
    ENV_PARES_ERROR(-15, "envEnum配置不合法"),
    APP_CODE_PARES_ERROR(-16, "appCode配置不合法"),
    SERVER_URL_PARES_ERROR(-17, "serverUrl配置不合法"),
    APP_NAME_PARES_ERROR(-18, "appName配置不合法"),
    PARSE_PARES_ERROR(-19, "解析全局配置失败"),
    FILE_CREATE_ERROR(-20, "文件创建失败"),
    PROPERTIES_WRITE_ERROR(-21, "properties写入失败"),
    STREAM_CLOSE_ERROR(-22, "流关闭异常"),
    FILE_NOT_EXIST_ERROR(-23, "文件不存在"),
    ZK_PATH_ERROR(-24, "获取zookeeper地址失败，请检查控制台是否启动"),
    LOAD_ERROR(-25, "加载本地配置文件失败"),
    FILE_TYPE_ERROR(-25, "文件类型不合法"),
    PROXY_PARSE_ERROR(-26, "代理类转换异常"),
    STATIC_INJECT_ERROR(-27, "无法注入静态属性"),
    FILE_DELETE_ERROR(-28, "无法删除文件"),

    ;

    private int key;

    private String name;

    BridgeErrorEnum(int key, String name) {
        this.key = key;
        this.name = name;
    }

    /**
     * 状态值
     */
    @Override
    public int getCode() {
        return this.key;
    }

    /**
     * 状态描述
     */
    @Override
    public String getMessage() {
        return this.name;
    }
}
