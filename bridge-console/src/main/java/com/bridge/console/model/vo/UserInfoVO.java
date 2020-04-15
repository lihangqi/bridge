package com.bridge.console.model.vo;

import com.bridge.console.service.account.SessionContext;
import lombok.Data;

import java.util.List;

/**
 * @author Jay
 * @version v1.0
 * @description 请添加类描述
 * @date 2019-03-11 10:16
 */
@Data
public class UserInfoVO extends SessionContext {

    /**
     * 团队名称
     */
    private String teamName;


    /**
     * 权限列表
     */
    private List<String> permissionList;
}
