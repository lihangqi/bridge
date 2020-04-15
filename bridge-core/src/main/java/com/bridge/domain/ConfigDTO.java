package com.bridge.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author Jay
 * @version v1.0
 * @description 键值对
 * @date 2019-01-04 11:34
 */
@Data
public class ConfigDTO implements Serializable {

    private static final long serialVersionUID = -3230132177302062572L;

    private List<ConfigKv> configList;

}
