package cn.phone.repair.module.repair.service.impl;

import cn.phone.repair.framework.common.biz.system.permission.PermissionCommonApi;
import cn.phone.repair.framework.common.util.object.BeanUtils;
import cn.phone.repair.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.phone.repair.framework.security.core.util.SecurityFrameworkUtils;
import cn.phone.repair.module.repair.controller.admin.vo.accessory.AccessoryFlowRespVO;
import cn.phone.repair.module.repair.controller.admin.vo.accessory.AccessoryReturnSaveReqVO;
import cn.phone.repair.module.repair.controller.admin.vo.accessory.AccessoryUseSaveReqVO;
import cn.phone.repair.module.repair.dal.dataobject.AccessoryDO;
import cn.phone.repair.module.repair.dal.dataobject.AccessoryFlowDO;
import cn.phone.repair.module.repair.dal.dataobject.RepairOrderDO;
import cn.phone.repair.module.repair.dal.mysql.AccessoryFlowMapper;
import cn.phone.repair.module.repair.dal.mysql.AccessoryMapper;
import cn.phone.repair.module.repair.dal.mysql.RepairOrderMapper;
import cn.phone.repair.module.repair.service.AccessoryFlowService;
import cn.phone.repair.module.repair.service.AccessoryService;
import cn.phone.repair.module.system.api.dept.DeptApi;
import cn.phone.repair.module.system.api.dept.dto.DeptRespDTO;
import cn.phone.repair.module.system.api.user.AdminUserApi;
import cn.phone.repair.module.system.api.user.dto.AdminUserRespDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import cn.phone.repair.module.repair.dal.dataobject.AccessoryDeptStockDO;
import cn.phone.repair.module.repair.dal.mysql.AccessoryDeptStockMapper;

@Service
@Slf4j
public class AccessoryFlowServiceImpl implements AccessoryFlowService {

    @Resource
    private AccessoryFlowMapper accessoryFlowMapper;

    @Resource
    private AccessoryMapper accessoryMapper;

    @Resource
    private AccessoryDeptStockMapper accessoryDeptStockMapper;

    @Resource
    private RepairOrderMapper repairOrderMapper;

    @Resource
    private AccessoryService accessoryService;

    @Resource
    private AdminUserApi adminUserApi;

    @Resource
    private DeptApi deptApi;

    @Resource
    private PermissionCommonApi permissionCommonApi;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createAccessoryUse(AccessoryUseSaveReqVO reqVO) {
        RepairOrderDO repairOrder = repairOrderMapper.selectOne(new LambdaQueryWrapperX<RepairOrderDO>()
                .eq(RepairOrderDO::getOrderNo, reqVO.getRepairOrderNo()));
        if (repairOrder == null) {
            throw new RuntimeException("维修订单不存在");
        }
        if ("completed".equals(repairOrder.getStatus()) || "cancelled".equals(repairOrder.getStatus())) {
            throw new RuntimeException("维修订单已完成或已取消，无法领用配件");
        }

        AccessoryDO accessory = accessoryMapper.selectById(reqVO.getAccessoryId());
        if (accessory == null) {
            throw new RuntimeException("配件不存在");
        }

        Long userId = SecurityFrameworkUtils.getLoginUserId();
        AdminUserRespDTO user = adminUserApi.getUser(userId).getCheckedData();
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        AccessoryDeptStockDO stock = accessoryDeptStockMapper.selectByAccessoryIdAndDeptId(
                reqVO.getAccessoryId(), user.getDeptId());
        if (stock == null || stock.getAvailableStock() < reqVO.getQuantity()) {
            throw new RuntimeException("库存不足");
        }

        accessoryService.updateAccessoryStock(reqVO.getAccessoryId(), reqVO.getQuantity(), false, user.getDeptId());

        AccessoryFlowDO flow = new AccessoryFlowDO();
        flow.setAccessoryId(reqVO.getAccessoryId());
        flow.setRepairOrderNo(repairOrder.getOrderNo());
        flow.setType("use");
        flow.setQuantity(reqVO.getQuantity());
        flow.setOperatorId(user.getId());
        flow.setOperator(user.getNickname());
        flow.setDeptId(user.getDeptId());
        flow.setReason(reqVO.getReason());
        flow.setRemark(reqVO.getRemark());
        flow.setCreateTime(LocalDateTime.now());
        flow.setUpdateTime(LocalDateTime.now());
        accessoryFlowMapper.insert(flow);

        return flow.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createAccessoryReturn(AccessoryReturnSaveReqVO reqVO) {
        RepairOrderDO repairOrder = repairOrderMapper.selectOne(new LambdaQueryWrapperX<RepairOrderDO>()
                .eq(RepairOrderDO::getOrderNo, reqVO.getRepairOrderNo()));
        if (repairOrder == null) {
            throw new RuntimeException("维修订单不存在");
        }

        AccessoryDO accessory = accessoryMapper.selectById(reqVO.getAccessoryId());
        if (accessory == null) {
            throw new RuntimeException("配件不存在");
        }

        Long userId = SecurityFrameworkUtils.getLoginUserId();
        AdminUserRespDTO user = adminUserApi.getUser(userId).getCheckedData();
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        Integer availableReturnQuantity = getAvailableReturnQuantity(repairOrder.getOrderNo(), reqVO.getAccessoryId());
        if (reqVO.getQuantity() > availableReturnQuantity) {
            throw new RuntimeException("退库数量超过可退数量");
        }

        if (!reqVO.getIsQualityIssue()) {
            accessoryService.updateAccessoryStock(reqVO.getAccessoryId(), reqVO.getQuantity(), true, user.getDeptId());
        }

        AccessoryFlowDO flow = new AccessoryFlowDO();
        flow.setAccessoryId(reqVO.getAccessoryId());
        flow.setRepairOrderNo(repairOrder.getOrderNo());
        flow.setType("return");
        flow.setQuantity(reqVO.getQuantity());
        flow.setOperatorId(user.getId());
        flow.setOperator(user.getNickname());
        flow.setDeptId(user.getDeptId());
        flow.setReason(reqVO.getReason());
        flow.setRemark(reqVO.getRemark());
        flow.setCreateTime(LocalDateTime.now());
        flow.setUpdateTime(LocalDateTime.now());
        accessoryFlowMapper.insert(flow);

        return flow.getId();
    }

    public List<AccessoryFlowRespVO> getAccessoryFlowListByRepairOrderNo(String repairOrderNo) {
        List<AccessoryFlowDO> flows = accessoryFlowMapper.selectListByRepairOrderNo(repairOrderNo);

        Long userId = SecurityFrameworkUtils.getLoginUserId();
        AdminUserRespDTO user = adminUserApi.getUser(userId).getCheckedData();
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        if (isAdmin(userId)) {
            return buildAccessoryFlowRespVOList(flows, true);
        }

        Long deptId = user.getDeptId();
        boolean isDeptManager = isDeptManager(userId, deptId);

        List<AccessoryFlowDO> filteredFlows;
        if (isDeptManager) {
            filteredFlows = flows.stream()
                    .filter(flow -> flow.getDeptId() != null && flow.getDeptId().equals(deptId))
                    .collect(Collectors.toList());
        } else {
            filteredFlows = flows.stream()
                    .filter(flow -> flow.getOperatorId().equals(userId))
                    .collect(Collectors.toList());
        }
        return buildAccessoryFlowRespVOList(filteredFlows, false);
    }

    @Override
    public List<AccessoryFlowRespVO> getAccessoryFlowListByAccessoryId(Long accessoryId) {
        AccessoryDO accessory = accessoryMapper.selectById(accessoryId);
        if (accessory == null) {
            throw new RuntimeException("配件不存在");
        }

        Long userId = SecurityFrameworkUtils.getLoginUserId();
        AdminUserRespDTO user = adminUserApi.getUser(userId).getCheckedData();
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        if (!isAdmin(userId)) {
            AccessoryDeptStockDO stock = accessoryDeptStockMapper.selectByAccessoryIdAndDeptId(
                    accessoryId, user.getDeptId());
            if (stock == null) {
                throw new RuntimeException("无权查看其他部门的配件流水");
            }
        }

        List<AccessoryFlowDO> flows = accessoryFlowMapper.selectListByAccessoryId(accessoryId);

        Long deptId = user.getDeptId();
        boolean isDeptManager = isDeptManager(userId, deptId);

        if (!isAdmin(userId) && !isDeptManager) {
            flows = flows.stream()
                    .filter(flow -> flow.getOperatorId().equals(userId))
                    .collect(Collectors.toList());
        }

        return buildAccessoryFlowRespVOList(flows, isAdmin(userId));
    }

    @Override
    public Integer getAvailableReturnQuantity(String repairOrderNo, Long accessoryId) {
        AccessoryDO accessory = accessoryMapper.selectById(accessoryId);
        if (accessory == null) {
            throw new RuntimeException("配件不存在");
        }

        Long userId = SecurityFrameworkUtils.getLoginUserId();
        AdminUserRespDTO user = adminUserApi.getUser(userId).getCheckedData();
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        AccessoryDeptStockDO stock = accessoryDeptStockMapper.selectByAccessoryIdAndDeptId(
                accessoryId, user.getDeptId());
        if (stock == null) {
            throw new RuntimeException("无权操作其他部门的配件");
        }

        List<AccessoryFlowDO> flows = accessoryFlowMapper.selectListByRepairOrderNo(repairOrderNo);
        int useQuantity = 0;
        int returnQuantity = 0;

        for (AccessoryFlowDO flow : flows) {
            if (flow.getAccessoryId().equals(accessoryId)) {
                if ("use".equals(flow.getType())) {
                    useQuantity += flow.getQuantity();
                } else if ("return".equals(flow.getType())) {
                    returnQuantity += flow.getQuantity();
                }
            }
        }

        return useQuantity - returnQuantity;
    }

    public List<AccessoryFlowRespVO> getAllAccessoryFlowList() {
        List<AccessoryFlowDO> flows = accessoryFlowMapper.selectList();

        Long userId = SecurityFrameworkUtils.getLoginUserId();
        AdminUserRespDTO user = adminUserApi.getUser(userId).getCheckedData();
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        if (isAdmin(userId)) {
            return buildAccessoryFlowRespVOList(flows, true);
        }

        Long deptId = user.getDeptId();
        boolean isDeptManager = isDeptManager(userId, deptId);

        List<AccessoryFlowDO> filteredFlows;
        if (isDeptManager) {
            filteredFlows = flows.stream()
                    .filter(flow -> flow.getDeptId() != null && flow.getDeptId().equals(deptId))
                    .collect(Collectors.toList());
        } else {
            filteredFlows = flows.stream()
                    .filter(flow -> flow.getOperatorId().equals(userId))
                    .collect(Collectors.toList());
        }

        return buildAccessoryFlowRespVOList(filteredFlows, false);
    }

    private boolean isAdmin(Long userId) {
        return SecurityFrameworkUtils.getLoginUserDeptId() == null;
    }

    private boolean isDeptManager(Long userId, Long deptId) {
        if (deptId == null) {
            return false;
        }

        Boolean hasRole = permissionCommonApi.hasAnyRoles(userId, "manager").getCheckedData();
        return Boolean.TRUE.equals(hasRole);
    }
    private List<AccessoryFlowRespVO> buildAccessoryFlowRespVOList(List<AccessoryFlowDO> flows, boolean showDeptName) {
        List<AccessoryFlowRespVO> respVOs = new ArrayList<>();
        for (AccessoryFlowDO flow : flows) {
            AccessoryFlowRespVO respVO = BeanUtils.toBean(flow, AccessoryFlowRespVO.class);
            if (respVO == null) {
                continue;
            }

            AccessoryDO accessory = accessoryMapper.selectById(flow.getAccessoryId());
            if (accessory != null) {
                respVO.setAccessoryName(accessory.getName());
                respVO.setAccessorySpec(accessory.getSpec());
            }

            if (showDeptName && flow.getDeptId() != null) {
                DeptRespDTO dept = deptApi.getDept(flow.getDeptId()).getCheckedData();
                if (dept != null) {
                    respVO.setDeptName(dept.getName());
                }
            }

            respVOs.add(respVO);
        }
        return respVOs;
    }

}

