package com.bridge.console.web;

import com.bridge.console.annotation.NotCertification;
import com.bridge.console.model.vo.ZkDataVO;
import com.bridge.console.service.config.ConfigService;
import com.bridge.console.service.zk.ZookeeperComponent;
import com.bridge.console.utils.ex.BusinessCheckFailException;
import com.bridge.console.utils.result.BaseErrorEnum;
import com.bridge.console.utils.result.Result;
import com.bridge.enums.EnvEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Jay
 * @version v1.0
 * @description 请添加类描述
 * @date 2019-02-26 17:17
 */
@RestController
public class ZookeeperController extends BaseController {


    @Autowired
    private ConfigService configService;

    @Value("${zk.address}")
    private String zkAddress;

    @Autowired
    private ZookeeperComponent zookeeperComponent;

    /**
     * 查询zk节点数据
     *
     * @param appId
     * @return
     */
    @ResponseBody
    @RequestMapping("/queryZkDataList")
    public Result<List<ZkDataVO>> queryZkDataList(Integer appId, Integer envId) {
        return Result.wrapSuccessfulResult(configService.queryZkDataList(appId, envId));
    }


    /**
     * 查询zk地址
     *
     * @return
     */
    @NotCertification
    @ResponseBody
    @RequestMapping("/queryZkAddress")
    public Result<String> queryZkAddress() {
        return Result.wrapSuccessfulResult(zkAddress);
    }


    /**
     * 同步db数据到zk
     *
     * @param appId
     * @return
     */
    @ResponseBody
    @RequestMapping("/consistencyDbToZk")
    public Result<Boolean> consistencyDbToZk(Integer appId, Integer envId) {
        if (appId == null || envId == null) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "入参不能为空");
        }
        if (EnvEnum.getEnvEnum(envId) == null) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "envId 不合法");
        }
        Integer operatorId = super.getSessionContext().getId();
        Integer roleType = super.getSessionContext().getAccountRole();
        Integer operatorTeamId = super.getSessionContext().getTeamId();
        configService.permissionCheck(operatorId, appId, roleType, operatorTeamId);
        zookeeperComponent.consistencyDbAndZk(envId, appId);
        return Result.wrapSuccessfulResult(Boolean.TRUE);
    }
}
