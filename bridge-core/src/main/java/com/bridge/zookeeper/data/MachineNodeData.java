package com.bridge.zookeeper.data;

import com.bridge.enums.NeedUpdateEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Jay
 * @version v1.0
 * @description 请添加类描述
 * @date 2019-02-13 18:27
 */
@Data
public class MachineNodeData implements Serializable {

    private static final long serialVersionUID = -4267883737945793439L;

    /**
     * 是否需要更新 {@link NeedUpdateEnum}
     */
    private Integer needUpdate;

    /**
     * 服务url
     */
    private String machineHost;

    /**
     * 客户端持有的key版本号
     */
    private String version;
}
