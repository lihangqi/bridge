package com.bridge.console.model.vo;

import lombok.Data;

import java.util.List;

/**
 * @author Jay
 * @version v1.0
 * @description 请添加类描述
 * @date 2019-01-25 18:25
 */
@Data
public class TeamAndAccountVO {


    /**
     * 团队列表k/v
     */
    List<EnumVO> teamList;


    /**
     * 账号列表k/v
     */
    List<EnumVO> accountList;

}
