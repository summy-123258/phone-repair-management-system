package cn.phone.repair.module.repair.service;

import cn.phone.repair.module.repair.controller.admin.vo.accessory.AccessoryWarningRespVO;

import java.util.List;

/**
 * 配件预警服务接口
 * 提供配件预警的管理和查询功能
 */
public interface AccessoryWarningService {

    /**
     * 获取预警列表
     * @return 预警列表
     */
    List<AccessoryWarningRespVO> getAccessoryWarningList();

    /**
     * 根据状态获取预警列表
     * @param status 状态
     * @return 预警列表
     */
    List<AccessoryWarningRespVO> getAccessoryWarningListByStatus(String status);

    /**
     * 更新预警状态
     * @param id 预警ID
     * @param status 状态
     */
    void updateWarningStatus(Long id, String status);

    /**
     * 检查所有配件预警
     */
    void checkAllAccessoryWarnings();
}
