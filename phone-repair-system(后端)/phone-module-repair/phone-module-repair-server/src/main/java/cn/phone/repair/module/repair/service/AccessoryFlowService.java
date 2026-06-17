package cn.phone.repair.module.repair.service;

import cn.phone.repair.module.repair.controller.admin.vo.accessory.AccessoryFlowRespVO;
import cn.phone.repair.module.repair.controller.admin.vo.accessory.AccessoryReturnSaveReqVO;
import cn.phone.repair.module.repair.controller.admin.vo.accessory.AccessoryUseSaveReqVO;

import java.util.List;

/**
 * 配件流水服务接口
 * 提供配件的领用和退库操作，以及流水查询功能
 */
public interface AccessoryFlowService {

    /**
     * 领用配件
     * @param reqVO 领用请求
     * @return 流水ID
     */
    Long createAccessoryUse(AccessoryUseSaveReqVO reqVO);

    /**
     * 退库配件
     * @param reqVO 退库请求
     * @return 流水ID
     */
    Long createAccessoryReturn(AccessoryReturnSaveReqVO reqVO);

    /**
     * 根据维修单号获取配件流水
     * @param repairOrderNo 维修单号
     * @return 流水列表
     */
    List<AccessoryFlowRespVO> getAccessoryFlowListByRepairOrderNo(String repairOrderNo);

    /**
     * 根据配件ID获取流水
     * @param accessoryId 配件ID
     * @return 流水列表
     */
    List<AccessoryFlowRespVO> getAccessoryFlowListByAccessoryId(Long accessoryId);

    /**
     * 获取可退数量
     * @param repairOrderNo 维修单号
     * @param accessoryId 配件ID
     * @return 可退数量
     */
    Integer getAvailableReturnQuantity(String repairOrderNo, Long accessoryId);
    /**
     * 获取所有配件流水
     * @return 流水列表
     */
    List<AccessoryFlowRespVO> getAllAccessoryFlowList();
}
