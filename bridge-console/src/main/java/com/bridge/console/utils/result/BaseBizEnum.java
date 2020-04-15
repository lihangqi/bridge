package com.bridge.console.utils.result;

import lombok.Getter;

/**
 * @author Jay
 * @version v1.0
 * @description 请添加类描述
 * @date 2019-01-21 21:11
 */
public enum BaseBizEnum {
    // 零
    ZERO(0, "零"),
    // 一
    FIRST(1, "一"),
    // 二
    SECOND(2, "二"),
    // 三
    THREE(3, "三"),
    // 四
    FOUR(4, "四"),
    // 五
    FIVE(5, "五"),
    // 六
    SIX(6, "六"),
    // 调用成功
    OK(800, "调用成功"),
    // 已删除
    YN_Y(BaseBizEnum.FIRST.getCode(), "已删除"),
    // 未删除
    YN_N(BaseBizEnum.ZERO.getCode(), "未删除"),
    // 系统管理员
    DEFAULT_USER(BaseBizEnum.ZERO.getCode(), "系统管理员"),
    // 保存成功
    SAVE_SUCCESS(BaseBizEnum.OK.getCode(), "保存成功！"),
    // 更新成功
    UPDATE_SUCCESS(BaseBizEnum.OK.getCode(), "更新成功"),
    // 删除成功
    DELETE_SUCCESS(BaseBizEnum.OK.getCode(), "删除成功");


    @Getter
    private int code;

    @Getter
    private String message;

    BaseBizEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

}