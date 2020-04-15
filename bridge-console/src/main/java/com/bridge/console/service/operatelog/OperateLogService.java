package com.bridge.console.service.operatelog;

import com.bridge.console.model.entity.ConfigOperateLogDO;
import com.bridge.console.model.enums.AccountRoleEnum;
import com.bridge.console.model.vo.WorkSpaceVO;

/**
 * @author Jay
 * @version v1.0
 * @description 操作日志
 * @date 2019-02-20 14:55
 */
public interface OperateLogService {


    /**
     * 回滚数据
     *
     * @param id
     * @param operatorId
     * @param roleType
     * @param operatorTeamId
     * @return
     */
    Boolean rollbackData(Integer id, Integer operatorId, Integer roleType, Integer operatorTeamId);


    /**
     * 写入日志表
     *
     * @param configOperateLogDO
     */
    void writeLog(ConfigOperateLogDO configOperateLogDO);


    /**
     * 查询工作台数据
     *
     * @param accountId
     * @param accountRoleEnum
     * @param teamId
     * @param envId
     * @return
     */
    WorkSpaceVO getWorkSpaceInfo(Integer teamId, Integer accountId, AccountRoleEnum accountRoleEnum,Integer envId);
}
