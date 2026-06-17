package cn.phone.repair.module.repair.service;

import cn.phone.repair.module.repair.controller.admin.vo.repair.RepairProgressSaveReqVO;
import cn.phone.repair.module.repair.dal.dataobject.RepairProgressDO;

import java.util.List;

public interface RepairProgressService {

    Long createRepairProgress(RepairProgressSaveReqVO createReqVO);

    void deleteRepairProgress(Long id);

    List<RepairProgressDO> getRepairProgressListByOrderId(Long orderId);
}
