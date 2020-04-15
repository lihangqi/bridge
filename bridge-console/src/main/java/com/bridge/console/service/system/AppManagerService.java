package com.bridge.console.service.system;

import com.bridge.console.model.vo.AppDefEditOrAddVO;

/**
 * @author Jay
 * @version v1.0
 * @description 应用管理的服务
 * @date 2019-01-22 16:23
 */
public interface AppManagerService {


    /**
     * 应用修改
     *
     * @param appDefEditOrAddVO
     * @param modifier
     * @return
     */
    Boolean editApp(AppDefEditOrAddVO appDefEditOrAddVO, Integer modifier);


    /**
     * 应用新增
     *
     * @param operateId
     * @param appDefEditOrAddVO
     * @return
     */
    Boolean addApp(AppDefEditOrAddVO appDefEditOrAddVO, Integer operateId);


    /**
     * 删除应用
     *
     * @param id
     * @param operateId
     * @return
     */
    Boolean deleteApp(Integer id, Integer operateId);
}
