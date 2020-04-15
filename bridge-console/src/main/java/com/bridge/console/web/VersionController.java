package com.bridge.console.web;

import com.bridge.console.annotation.NotCertification;
import com.bridge.console.utils.result.Result;
import com.bridge.version.Version;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Jay
 * @version v1.0
 * @description 系统版本
 * @date 2019-12-17 17:50
 */
@RestController
public class VersionController {


    /**
     * 获取系统版本号
     *
     * @return
     */
    @NotCertification
    @ResponseBody
    @RequestMapping("/getSysVersion")
    public Result<String> pushLog() {
        return Result.wrapSuccessfulResult(Version.getVersion());
    }
}
