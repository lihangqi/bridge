package com.bridge.console.model.enums;


import com.bridge.console.utils.KeyedNamed;
import lombok.Getter;

/**
 * @author Jay
 * @version v1.0
 * @description 账号角色枚举
 * @date 2019-01-22 17:12
 */
public enum AccountRoleEnum implements KeyedNamed {

    // 管理员
    ROLE_ADMIN(0, "系统管理员"),
    // 普通用户
    ROLE_USER(1, "普通用户"),
    // 团队leader
    ROLE_TEAM_LEADER(2, "团队管理员"),
    ;

    AccountRoleEnum(Integer key, String name) {
        this.key = key;
        this.name = name;
    }

    @Getter
    private int key;

    @Getter
    private String name;

}
