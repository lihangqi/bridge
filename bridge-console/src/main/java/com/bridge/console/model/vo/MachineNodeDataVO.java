package com.bridge.console.model.vo;

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
public class MachineNodeDataVO implements Serializable {

    private static final long serialVersionUID = -4267883737945793439L;

    /**
     * 是否需要更新 {@link NeedUpdateEnum}
     */
    private Integer needUpdate;

    /**
     * 实例url
     */
    private String machineHost;
}
