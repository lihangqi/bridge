package com.bridge.console.model.vo;

import com.bridge.zookeeper.data.MachineNodeData;
import lombok.Data;

import java.util.List;

/**
 * @author Jay
 * @version v1.0
 * @description 请添加类描述
 * @date 2019-02-26 17:19
 */
@Data
public class ZkDataVO {


    private String key;


    private String value;


    private String version;


    private List<MachineNodeData> machineNodeDataList;
}
