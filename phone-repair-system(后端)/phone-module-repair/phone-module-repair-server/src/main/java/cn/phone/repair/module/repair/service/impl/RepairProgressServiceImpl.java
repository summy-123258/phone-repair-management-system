package cn.phone.repair.module.repair.service.impl;

import cn.phone.repair.framework.common.util.object.BeanUtils;
import cn.phone.repair.module.repair.controller.admin.vo.repair.RepairProgressSaveReqVO;
import cn.phone.repair.module.repair.dal.dataobject.RepairProgressDO;
import cn.phone.repair.module.repair.dal.mysql.RepairProgressMapper;
import cn.phone.repair.module.repair.service.RepairProgressService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class RepairProgressServiceImpl implements RepairProgressService {

    @Resource
    private RepairProgressMapper repairProgressMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createRepairProgress(RepairProgressSaveReqVO createReqVO) {
        RepairProgressDO progress = BeanUtils.toBean(createReqVO, RepairProgressDO.class);
        if (progress.getTime() == null) {
            progress.setTime(LocalDateTime.now());
        }
        repairProgressMapper.insert(progress);
        return progress.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRepairProgress(Long id) {
        repairProgressMapper.deleteById(id);
    }

    @Override
    public List<RepairProgressDO> getRepairProgressListByOrderId(Long orderId) {
        return repairProgressMapper.selectListByField(RepairProgressDO::getOrderId, orderId);
    }
}
