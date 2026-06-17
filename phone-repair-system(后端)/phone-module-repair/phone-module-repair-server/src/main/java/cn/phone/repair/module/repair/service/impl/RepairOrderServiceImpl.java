package cn.phone.repair.module.repair.service.impl;

import cn.phone.repair.framework.common.pojo.CommonResult;
import cn.phone.repair.framework.common.pojo.PageResult;
import cn.phone.repair.framework.common.util.object.BeanUtils;
import cn.phone.repair.framework.security.core.LoginUser;
import cn.phone.repair.framework.security.core.util.SecurityFrameworkUtils;
import cn.phone.repair.module.repair.controller.admin.vo.repair.RepairOrderPageReqVO;
import cn.phone.repair.module.repair.controller.admin.vo.repair.RepairOrderRespVO;
import cn.phone.repair.module.repair.controller.admin.vo.repair.RepairOrderSaveReqVO;
import cn.phone.repair.module.repair.dal.dataobject.CustomerDO;
import cn.phone.repair.module.repair.dal.dataobject.RepairOrderDO;
import cn.phone.repair.module.repair.dal.mysql.RepairOrderMapper;
import cn.phone.repair.module.repair.enums.RepairOrderStatusEnum;
import cn.phone.repair.module.repair.service.CustomerService;
import cn.phone.repair.module.repair.service.RepairOrderService;
import cn.phone.repair.module.system.api.dept.DeptApi;
import cn.phone.repair.module.system.api.dept.dto.DeptRespDTO;
import cn.phone.repair.module.system.api.user.AdminUserApi;
import cn.phone.repair.module.system.api.user.dto.AdminUserRespDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RepairOrderServiceImpl implements RepairOrderService {

    @Resource
    private RepairOrderMapper repairOrderMapper;

    @Resource
    private CustomerService customerService;

    @Resource
    private AdminUserApi adminUserApi;

    @Resource
    private DeptApi deptApi;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createRepairOrder(RepairOrderSaveReqVO createReqVO) {
        // 获取或创建客户
        CustomerDO customer = customerService.getOrCreateCustomer(createReqVO.getPhone(), createReqVO.getName());

        RepairOrderDO repairOrder = BeanUtils.toBean(createReqVO, RepairOrderDO.class);
        repairOrder.setOrderNo(generateOrderNo());
        repairOrder.setStatus(RepairOrderStatusEnum.ACCEPTED.getStatus());
        repairOrder.setStatusText(RepairOrderStatusEnum.ACCEPTED.getName());
        repairOrder.setSubmitTime(LocalDateTime.now());
        // 设置部门ID（门店ID）
        LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
        if (loginUser != null && loginUser.getInfo() != null) {
            String deptIdStr = loginUser.getInfo().get(LoginUser.INFO_KEY_DEPT_ID);
            Long deptId = null;
            if (deptIdStr != null) {
                deptId = Long.valueOf(deptIdStr);
                repairOrder.setDeptId(deptId);
            }

            // 如果登录用户是维修人员，且没有指定维修人员，则自动设置为当前登录用户
            if (createReqVO.getStaffId() == null) {
                Long userId = loginUser.getId();
                if (userId != null) {
                    repairOrder.setStaffId(userId);
                }
            } else {
                // 验证店长只能选择对应门店的维修人员
                validateStaffDept(createReqVO.getStaffId(), deptId);
            }
        }

        // 设置客户ID
        repairOrder.setCustomerId(customer.getId());
        repairOrderMapper.insert(repairOrder);
        return repairOrder.getId();
    }

    /**
     * 验证维修人员是否属于指定部门
     * @param staffId 维修人员ID
     * @param deptId 部门ID
     */
    private void validateStaffDept(Long staffId, Long deptId) {
        if (staffId == null || deptId == null) {
            return;
        }

        // 获取当前登录用户
        LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
        if (loginUser == null) {
            return;
        }

        // 检查用户角色是否为店长
        boolean isStoreManager = false;
        List<String> scopes = loginUser.getScopes();
        if (scopes != null) {
            isStoreManager = scopes.stream()
                    .anyMatch(scope -> "STORE_MANAGER".equals(scope));
        }

        if (isStoreManager) {
            // 获取维修人员信息
            CommonResult<AdminUserRespDTO> result = adminUserApi.getUser(staffId);
            if (result != null && result.isSuccess()) {
                AdminUserRespDTO staff = result.getData();
                if (staff != null) {
                    // 检查维修人员的部门ID是否与当前登录用户的部门ID一致
                    if (!deptId.equals(staff.getDeptId())) {
                        throw new RuntimeException("只能选择本门店的维修人员");
                    }
                }
            }
        }
    }

    @Override
    public void updateRepairOrder(RepairOrderSaveReqVO updateReqVO) {
        RepairOrderDO existingOrder = repairOrderMapper.selectById(updateReqVO.getId());
        if (existingOrder == null) {
            throw new RuntimeException("维修单不存在");
        }

        // 如果手机号或姓名发生变化，更新客户信息
        if (updateReqVO.getPhone() != null || updateReqVO.getName() != null) {
            CustomerDO customer = customerService.getOrCreateCustomer(updateReqVO.getPhone(), updateReqVO.getName());
            updateReqVO.setCustomerId(customer.getId());
        }

        // 验证店长只能选择对应门店的维修人员
        if (updateReqVO.getStaffId() != null) {
            Long deptId = existingOrder.getDeptId();
            validateStaffDept(updateReqVO.getStaffId(), deptId);
        }

        BeanUtils.copyProperties(updateReqVO, existingOrder);
        repairOrderMapper.updateById(existingOrder);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRepairOrder(Long id) {
        repairOrderMapper.deleteById(id);
    }

    @Override
    public RepairOrderRespVO getRepairOrderWithDetails(Long id) {
        RepairOrderDO repairOrder = repairOrderMapper.selectById(id);
        if (repairOrder == null) {
            return null;
        }
        return buildRepairOrderRespVO(repairOrder);
    }

    @Override
    public PageResult<RepairOrderRespVO> getRepairOrderPageWithDetails(RepairOrderPageReqVO reqVO) {
        Long originalCustomerId = reqVO.getCustomerId();
        String customerName = reqVO.getName();
        Long originalStaffId = reqVO.getStaffId();
        String staffName = reqVO.getStaffName();

        if (customerName != null && !customerName.trim().isEmpty()) {
            List<CustomerDO> customers = customerService.searchCustomersByName(customerName);
            if (customers.isEmpty()) {
                return new PageResult<>(new ArrayList<>(), 0L);
            }
            List<Long> matchedCustomerIds = customers.stream()
                    .map(CustomerDO::getId)
                    .collect(Collectors.toList());

            if (originalCustomerId != null) {
                if (!matchedCustomerIds.contains(originalCustomerId)) {
                    return new PageResult<>(new ArrayList<>(), 0L);
                }
                reqVO.setCustomerId(originalCustomerId);
            } else {
                reqVO.setCustomerId(null);
            }
        }

        if (staffName != null && !staffName.trim().isEmpty()) {
            List<Long> matchedStaffIds = searchStaffIdsByName(staffName);
            if (matchedStaffIds.isEmpty()) {
                return new PageResult<>(new ArrayList<>(), 0L);
            }

            if (originalStaffId != null) {
                if (!matchedStaffIds.contains(originalStaffId)) {
                    return new PageResult<>(new ArrayList<>(), 0L);
                }
                reqVO.setStaffId(originalStaffId);
            } else {
                reqVO.setStaffId(null);
            }
        }

        PageResult<RepairOrderDO> pageResult = repairOrderMapper.selectPage(reqVO);
        List<RepairOrderDO> repairOrderList = pageResult.getList();

        if (repairOrderList.isEmpty()) {
            return new PageResult<>(new ArrayList<>(), pageResult.getTotal());
        }

        if (customerName != null && !customerName.trim().isEmpty()) {
            List<CustomerDO> customers = customerService.searchCustomersByName(customerName);
            List<Long> filteredCustomerIds = customers.stream()
                    .map(CustomerDO::getId)
                    .collect(Collectors.toList());
            repairOrderList = repairOrderList.stream()
                    .filter(order -> order.getCustomerId() != null && filteredCustomerIds.contains(order.getCustomerId()))
                    .collect(Collectors.toList());
        }

        if (staffName != null && !staffName.trim().isEmpty()) {
            List<Long> filteredStaffIds = searchStaffIdsByName(staffName);
            repairOrderList = repairOrderList.stream()
                    .filter(order -> order.getStaffId() != null && filteredStaffIds.contains(order.getStaffId()))
                    .collect(Collectors.toList());
        }

        List<Long> deptIds = repairOrderList.stream()
                .map(RepairOrderDO::getDeptId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());

        List<Long> customerIds = repairOrderList.stream()
                .map(RepairOrderDO::getCustomerId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());

        List<Long> staffIds = repairOrderList.stream()
                .map(RepairOrderDO::getStaffId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, DeptRespDTO> deptMap = new HashMap<>();
        if (!deptIds.isEmpty()) {
            deptMap = deptApi.getDeptMap(deptIds);
        }

        Map<Long, CustomerDO> customerMap = new HashMap<>();
        for (Long cid : customerIds) {
            CustomerDO customer = customerService.getCustomerById(cid);
            if (customer != null) {
                customerMap.put(cid, customer);
            }
        }

        Map<Long, AdminUserRespDTO> staffMap = new HashMap<>();
        for (Long sid : staffIds) {
            CommonResult<AdminUserRespDTO> result = adminUserApi.getUser(sid);
            if (result != null && result.isSuccess() && result.getData() != null) {
                staffMap.put(sid, result.getData());
            }
        }

        Map<Long, DeptRespDTO> finalDeptMap = deptMap;
        Map<Long, CustomerDO> finalCustomerMap = customerMap;
        Map<Long, AdminUserRespDTO> finalStaffMap = staffMap;

        List<RepairOrderRespVO> voList = repairOrderList.stream()
                .map(repairOrder -> buildRepairOrderRespVOWithMaps(repairOrder, finalDeptMap, finalCustomerMap, finalStaffMap))
                .collect(Collectors.toList());

        return new PageResult<>(voList, pageResult.getTotal());
    }

    private List<Long> searchStaffIdsByName(String staffName) {
        try {
            CommonResult<List<AdminUserRespDTO>> result = adminUserApi.getUserListByNickname(staffName);
            if (result != null && result.isSuccess() && result.getData() != null) {
                return result.getData().stream()
                        .map(AdminUserRespDTO::getId)
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            log.warn("搜索维修人员失败: {}", staffName, e);
        }
        return new ArrayList<>();
    }


    private RepairOrderRespVO buildRepairOrderRespVO(RepairOrderDO repairOrder) {
        RepairOrderRespVO respVO = BeanUtils.toBean(repairOrder, RepairOrderRespVO.class);
        if (respVO == null) {
            return null;
        }

        if (repairOrder.getDeptId() != null) {
            CommonResult<DeptRespDTO> result = deptApi.getDept(repairOrder.getDeptId());
            if (result != null && result.isSuccess() && result.getData() != null) {
                respVO.setStoreName(result.getData().getName());
            }
        }

        if (repairOrder.getCustomerId() != null) {
            CustomerDO customer = customerService.getCustomerById(repairOrder.getCustomerId());
            if (customer != null) {
                respVO.setName(customer.getName());
                respVO.setPhone(customer.getMobile());
            }
        }

        if (repairOrder.getStaffId() != null) {
            CommonResult<AdminUserRespDTO> result = adminUserApi.getUser(repairOrder.getStaffId());
            if (result != null && result.isSuccess() && result.getData() != null) {
                respVO.setStaffName(result.getData().getNickname());
            }
        }

        return respVO;
    }

    private RepairOrderRespVO buildRepairOrderRespVOWithMaps(RepairOrderDO repairOrder,
                                                             Map<Long, DeptRespDTO> deptMap,
                                                             Map<Long, CustomerDO> customerMap,
                                                             Map<Long, AdminUserRespDTO> staffMap) {
        RepairOrderRespVO respVO = BeanUtils.toBean(repairOrder, RepairOrderRespVO.class);
        if (respVO == null) {
            return null;
        }

        if (repairOrder.getDeptId() != null) {
            DeptRespDTO dept = deptMap.get(repairOrder.getDeptId());
            if (dept != null) {
                respVO.setStoreName(dept.getName());
            }
        }

        if (repairOrder.getCustomerId() != null) {
            CustomerDO customer = customerMap.get(repairOrder.getCustomerId());
            if (customer != null) {
                respVO.setName(customer.getName());
                respVO.setPhone(customer.getMobile());
            }
        }

        if (repairOrder.getStaffId() != null) {
            AdminUserRespDTO staff = staffMap.get(repairOrder.getStaffId());
            if (staff != null) {
                respVO.setStaffName(staff.getNickname());
            }
        }

        return respVO;
    }



    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRepairOrderStatus(Long id, String status, String statusText) {
        RepairOrderDO repairOrder = repairOrderMapper.selectById(id);
        if (repairOrder == null) {
            throw new RuntimeException("维修单不存在");
        }
        repairOrder.setStatus(status);
        repairOrder.setStatusText(statusText);

        if (RepairOrderStatusEnum.COMPLETED.getStatus().equals(status)) {
            repairOrder.setActualCompletionTime(LocalDateTime.now());
        }

        repairOrderMapper.updateById(repairOrder);
    }

    private String generateOrderNo() {
        String prefix = "WX";
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String random = String.valueOf((int) (Math.random() * 900000) + 100000);
        return prefix + date + random;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRepairOrderCost(Long id, BigDecimal cost) {
        RepairOrderDO repairOrder = repairOrderMapper.selectById(id);
        if (repairOrder == null) {
            throw new RuntimeException("维修单不存在");
        }
        repairOrder.setCost(cost);
        repairOrderMapper.updateById(repairOrder);
    }
}
