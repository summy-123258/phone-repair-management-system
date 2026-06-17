package cn.phone.repair.module.repair.service;

import cn.phone.repair.framework.common.pojo.PageResult;
import cn.phone.repair.module.repair.controller.admin.vo.repair.RepairOrderPageReqVO;
import cn.phone.repair.module.repair.controller.admin.vo.repair.RepairOrderRespVO;
import cn.phone.repair.module.repair.controller.admin.vo.repair.RepairOrderSaveReqVO;
import cn.phone.repair.module.repair.dal.dataobject.RepairOrderDO;

import javax.validation.Valid;
import java.math.BigDecimal;

public interface RepairOrderService {

    Long createRepairOrder(@Valid RepairOrderSaveReqVO createReqVO);

    void updateRepairOrder(@Valid RepairOrderSaveReqVO updateReqVO);

    void deleteRepairOrder(Long id);

    RepairOrderRespVO getRepairOrderWithDetails(Long id);

    PageResult<RepairOrderRespVO> getRepairOrderPageWithDetails(RepairOrderPageReqVO reqVO);

    void updateRepairOrderStatus(Long id, String status, String statusText);

    void updateRepairOrderCost(Long id, BigDecimal cost);
}
