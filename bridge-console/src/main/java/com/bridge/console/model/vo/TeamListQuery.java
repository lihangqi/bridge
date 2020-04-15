package com.bridge.console.model.vo;

import com.bridge.console.utils.result.BasePageQueryParam;
import lombok.Data;

/**
 * @author Jay
 * @version v1.0
 * @description 请添加类描述
 * @date 2019-01-23 13:48
 */
@Data
public class TeamListQuery extends BasePageQueryParam {


    /**
     * 团队名称
     */
    private String teamName;
}
