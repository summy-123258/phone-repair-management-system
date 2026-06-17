package cn.phone.repair.module.repair.service.impl;

import cn.phone.repair.framework.common.pojo.PageResult;
import cn.phone.repair.framework.security.core.util.SecurityFrameworkUtils;
import cn.phone.repair.module.repair.controller.admin.vo.accessory.AccessoryRespVO;
import cn.phone.repair.module.repair.controller.admin.vo.accessory.AccessorySaveReqVO;
import cn.phone.repair.module.repair.dal.dataobject.AccessoryDO;
import cn.phone.repair.module.repair.dal.dataobject.AccessoryDeptStockDO;
import cn.phone.repair.module.repair.dal.dataobject.AccessoryWarningDO;
import cn.phone.repair.module.repair.dal.mysql.AccessoryDeptStockMapper;
import cn.phone.repair.module.repair.dal.mysql.AccessoryMapper;
import cn.phone.repair.module.repair.dal.mysql.AccessoryWarningMapper;
import cn.phone.repair.module.repair.service.AccessoryService;
import cn.phone.repair.module.system.api.dept.DeptApi;
import cn.phone.repair.module.system.api.dept.dto.DeptRespDTO;
import cn.phone.repair.module.system.api.permission.PermissionApi;
import cn.phone.repair.module.system.api.user.AdminUserApi;
import cn.phone.repair.module.system.api.user.dto.AdminUserRespDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AccessoryServiceImpl implements AccessoryService {

    @Resource
    private AccessoryMapper accessoryMapper;

    @Resource
    private AccessoryDeptStockMapper accessoryDeptStockMapper;

    @Resource
    private AccessoryWarningMapper accessoryWarningMapper;

    @Resource
    private AdminUserApi adminUserApi;

    @Resource
    private DeptApi deptApi;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createAccessory(AccessorySaveReqVO createReqVO) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        AdminUserRespDTO user = adminUserApi.getUser(userId).getCheckedData();
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        AccessoryDO accessory = new AccessoryDO();
        accessory.setName(createReqVO.getName());
        accessory.setSpec(createReqVO.getSpec());
        accessory.setUnit(createReqVO.getUnit());
        accessory.setSafeStock(createReqVO.getSafeStock());
        accessory.setMaxStock(createReqVO.getMaxStock());
        accessory.setSupplier(createReqVO.getSupplier());
        accessory.setRemark(createReqVO.getRemark());
        accessory.setCreateTime(LocalDateTime.now());
        accessory.setUpdateTime(LocalDateTime.now());
        accessoryMapper.insert(accessory);

        AccessoryDeptStockDO stock = new AccessoryDeptStockDO();
        stock.setAccessoryId(accessory.getId());
        Long targetDeptId = createReqVO.getDeptId();
        if (targetDeptId == null) {
            targetDeptId = user.getDeptId();
        }
        stock.setDeptId(targetDeptId);
        stock.setTotalStock(createReqVO.getTotalStock() != null ? createReqVO.getTotalStock() : 0);
        stock.setAvailableStock(createReqVO.getAvailableStock() != null ? createReqVO.getAvailableStock() : 0);
        stock.setLockedStock(0);
        stock.setCreateTime(LocalDateTime.now());
        stock.setUpdateTime(LocalDateTime.now());
        accessoryDeptStockMapper.insert(stock);

        return accessory.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAccessory(AccessorySaveReqVO updateReqVO) {
        AccessoryDO existingAccessory = accessoryMapper.selectById(updateReqVO.getId());
        if (existingAccessory == null) {
            throw new RuntimeException("配件不存在");
        }

        Long userId = SecurityFrameworkUtils.getLoginUserId();
        AdminUserRespDTO user = adminUserApi.getUser(userId).getCheckedData();
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        boolean isAdmin = isAdmin(user);

        if (!isAdmin) {
            AccessoryDeptStockDO stock = accessoryDeptStockMapper.selectByAccessoryIdAndDeptId(
                    updateReqVO.getId(), user.getDeptId());
            if (stock == null) {
                throw new RuntimeException("无权操作其他部门的配件");
            }
        }

        AccessoryDO accessory = new AccessoryDO();
        accessory.setId(updateReqVO.getId());
        accessory.setName(updateReqVO.getName());
        accessory.setSpec(updateReqVO.getSpec());
        accessory.setUnit(updateReqVO.getUnit());
        accessory.setSafeStock(updateReqVO.getSafeStock());
        accessory.setMaxStock(updateReqVO.getMaxStock());
        accessory.setSupplier(updateReqVO.getSupplier());
        accessory.setRemark(updateReqVO.getRemark());
        accessory.setUpdateTime(LocalDateTime.now());
        accessoryMapper.updateById(accessory);

        if ((updateReqVO.getTotalStock() != null || updateReqVO.getAvailableStock() != null)) {
            Long targetDeptId;

            if (isAdmin) {
                if (updateReqVO.getDeptId() == null) {
                    throw new RuntimeException("管理员调整库存需指定门店ID");
                }
                targetDeptId = updateReqVO.getDeptId();
            } else {
                targetDeptId = user.getDeptId();
            }

            AccessoryDeptStockDO stock = accessoryDeptStockMapper.selectByAccessoryIdAndDeptId(
                    updateReqVO.getId(), targetDeptId);
            if (stock == null) {
                stock = new AccessoryDeptStockDO();
                stock.setAccessoryId(updateReqVO.getId());
                stock.setDeptId(targetDeptId);
                stock.setTotalStock(0);
                stock.setAvailableStock(0);
                stock.setLockedStock(0);
                stock.setCreateTime(LocalDateTime.now());
                accessoryDeptStockMapper.insert(stock);
            }

            if (updateReqVO.getTotalStock() != null) {
                int diff = updateReqVO.getTotalStock() - stock.getTotalStock();
                stock.setTotalStock(updateReqVO.getTotalStock());
                stock.setAvailableStock(stock.getAvailableStock() + diff);
            }

            if (updateReqVO.getAvailableStock() != null) {
                stock.setAvailableStock(updateReqVO.getAvailableStock());
            }

            stock.setUpdateTime(LocalDateTime.now());
            accessoryDeptStockMapper.updateByAccessoryIdAndDeptId(stock);

            checkAndUpdateWarningStatus(updateReqVO.getId(), targetDeptId);
        }
    }



    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAccessory(Long id) {
        AccessoryDO accessory = accessoryMapper.selectById(id);
        if (accessory == null) {
            throw new RuntimeException("配件不存在");
        }
        accessoryMapper.deleteById(id);
    }

    @Override
    public AccessoryRespVO getAccessoryById(Long id) {
        AccessoryDO accessory = accessoryMapper.selectById(id);
        if (accessory == null) {
            return null;
        }

        Long userId = SecurityFrameworkUtils.getLoginUserId();
        AdminUserRespDTO user = adminUserApi.getUser(userId).getCheckedData();
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        return buildAccessoryRespVO(accessory, user, userId);
    }

    @Override
    public List<AccessoryRespVO> getAccessoryList() {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        AdminUserRespDTO user = adminUserApi.getUser(userId).getCheckedData();
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        List<AccessoryDO> accessories = accessoryMapper.selectList();

        return accessories.stream()
                .map(accessory -> buildAccessoryRespVO(accessory, user, userId))
                .filter(respVO -> respVO != null)
                .collect(Collectors.toList());
    }

    @Override
    public PageResult<AccessoryRespVO> getAccessoryListPage(Integer pageNum, Integer pageSize) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        AdminUserRespDTO user = adminUserApi.getUser(userId).getCheckedData();
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        int offset = (pageNum - 1) * pageSize;
        List<AccessoryDO> accessories = accessoryMapper.selectListPage(offset, pageSize);

        List<AccessoryRespVO> respVOs = accessories.stream()
                .map(accessory -> buildAccessoryRespVO(accessory, user, userId))
                .filter(respVO -> respVO != null)
                .collect(Collectors.toList());

        int total = accessoryMapper.selectCountByKeyword(null);

        return new PageResult<>(respVOs, (long) total);
    }

    @Override
    public List<AccessoryRespVO> getAccessoryListByKeyword(String keyword) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        AdminUserRespDTO user = adminUserApi.getUser(userId).getCheckedData();
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        List<AccessoryDO> accessories = accessoryMapper.selectListByKeyword(keyword);

        return accessories.stream()
                .map(accessory -> buildAccessoryRespVO(accessory, user, userId))
                .filter(respVO -> respVO != null)
                .collect(Collectors.toList());
    }

    @Override
    public PageResult<AccessoryRespVO> getAccessoryListByKeywordPage(String keyword, Integer pageNum, Integer pageSize) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        AdminUserRespDTO user = adminUserApi.getUser(userId).getCheckedData();
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        int offset = (pageNum - 1) * pageSize;
        List<AccessoryDO> accessories = accessoryMapper.selectListByKeywordPage(keyword, offset, pageSize);

        List<AccessoryRespVO> respVOs = accessories.stream()
                .map(accessory -> buildAccessoryRespVO(accessory, user, userId))
                .filter(respVO -> respVO != null)
                .collect(Collectors.toList());

        int total = accessoryMapper.selectCountByKeyword(keyword);

        return new PageResult<>(respVOs, (long) total);
    }

    @Override
    public void updateAccessoryStock(Long accessoryId, Integer quantity, boolean isIncrease, Long deptId) {
        AccessoryDO accessory = accessoryMapper.selectById(accessoryId);
        if (accessory == null) {
            throw new RuntimeException("配件不存在");
        }

        AccessoryDeptStockDO stock = accessoryDeptStockMapper.selectByAccessoryIdAndDeptId(
                accessoryId, deptId);
        if (stock == null) {
            throw new RuntimeException("配件库存不存在");
        }

        if (isIncrease) {
            stock.setTotalStock(stock.getTotalStock() + quantity);
            stock.setAvailableStock(stock.getAvailableStock() + quantity);
        } else {
            if (stock.getAvailableStock() < quantity) {
                throw new RuntimeException("库存不足");
            }
            stock.setAvailableStock(stock.getAvailableStock() - quantity);
            stock.setLockedStock(stock.getLockedStock() + quantity);
        }

        stock.setUpdateTime(LocalDateTime.now());
        accessoryDeptStockMapper.updateByAccessoryIdAndDeptId(stock);

        checkAndUpdateWarningStatus(accessoryId, deptId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void checkAndUpdateWarningStatus(Long accessoryId, Long deptId) {
        AccessoryDO accessory = accessoryMapper.selectById(accessoryId);
        if (accessory == null) {
            return;
        }

        AccessoryDeptStockDO stock = accessoryDeptStockMapper.selectByAccessoryIdAndDeptId(accessoryId, deptId);
        if (stock == null) {
            return;
        }

        int availableStock = stock.getAvailableStock();
        int safeStock = accessory.getSafeStock();

        String level;
        if (availableStock <= 0) {
            level = "severe";
        } else if (availableStock < safeStock) {
            level = "warning";
        } else {
            level = "normal";
        }

        AccessoryWarningDO warning = accessoryWarningMapper.selectByAccessoryId(accessoryId);
        if (warning == null) {
            // 仅当库存不足时才创建预警记录
            if (availableStock >= safeStock) {
                return;
            }
            warning = new AccessoryWarningDO();
            warning.setAccessoryId(accessoryId);
            warning.setDeptId(deptId);
            warning.setSafeStock(safeStock);
            warning.setCurrentStock(availableStock);
            warning.setLevel(level);
            warning.setStatus("active");
            warning.setCreateTime(LocalDateTime.now());
            warning.setUpdateTime(LocalDateTime.now());
            accessoryWarningMapper.insert(warning);
        } else {
            warning.setCurrentStock(availableStock);
            warning.setLevel(level);
            warning.setUpdateTime(LocalDateTime.now());
            // 当库存大于等于安全库存时，状态变为已解决
            if (availableStock >= safeStock && "active".equals(warning.getStatus())) {
                warning.setStatus("resolved");
            } else if (availableStock < safeStock && "resolved".equals(warning.getStatus())) {
                warning.setStatus("active");
            }
            accessoryWarningMapper.updateById(warning);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void replenishAccessory(Long accessoryId, Integer quantity, Long deptId) {
        AccessoryDO accessory = accessoryMapper.selectById(accessoryId);
        if (accessory == null) {
            throw new RuntimeException("配件不存在");
        }

        Long userId = SecurityFrameworkUtils.getLoginUserId();
        AdminUserRespDTO user = adminUserApi.getUser(userId).getCheckedData();
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 1. 确定目标门店 ID
        Long targetDeptId;
        // 如果是管理员且传入了 deptId，则使用传入的 deptId
        // 否则使用当前用户的部门 ID（店长只能补自己店的货）
        if (isAdmin(user) && deptId != null) {
            targetDeptId = deptId;
        } else {
            targetDeptId = user.getDeptId();
        }

        if (targetDeptId == null) {
            throw new RuntimeException("请指定补货门店");
        }

        // 2. 验证该门店是否有该配件库存
        AccessoryDeptStockDO stock = accessoryDeptStockMapper.selectByAccessoryIdAndDeptId(
                accessoryId, targetDeptId);

        if (stock == null) {
            // 若记录不存在，自动为该门店创建库存记录
            stock = new AccessoryDeptStockDO();
            stock.setAccessoryId(accessoryId);
            stock.setDeptId(targetDeptId);
            stock.setTotalStock(0);
            stock.setAvailableStock(0);
            stock.setLockedStock(0);
            stock.setCreateTime(LocalDateTime.now());
            stock.setUpdateTime(LocalDateTime.now());
            accessoryDeptStockMapper.insert(stock);
        }

        // 3. 执行库存更新
        updateAccessoryStock(accessoryId, quantity, true, targetDeptId);
    }

    private AccessoryRespVO buildAccessoryRespVO(AccessoryDO accessory, AdminUserRespDTO currentUser, Long userId) {
        if (accessory == null) {
            return null;
        }

        AccessoryRespVO respVO = new AccessoryRespVO();
        respVO.setId(accessory.getId());
        respVO.setName(accessory.getName());
        respVO.setSpec(accessory.getSpec());
        respVO.setUnit(accessory.getUnit());
        respVO.setSafeStock(accessory.getSafeStock());
        respVO.setMaxStock(accessory.getMaxStock());
        respVO.setSupplier(accessory.getSupplier());
        respVO.setRemark(accessory.getRemark());
        respVO.setCreateTime(accessory.getCreateTime());
        respVO.setUpdateTime(accessory.getUpdateTime());
        respVO.setCreator(accessory.getCreator());
        respVO.setUpdater(accessory.getUpdater());

        AdminUserRespDTO user = adminUserApi.getUser(userId).getCheckedData();

        boolean isAdmin = isAdmin(user);

        if (isAdmin) {
            List<AccessoryDeptStockDO> stocks = accessoryDeptStockMapper.selectListByAccessoryId(accessory.getId());
            int totalStock = stocks.stream().mapToInt(s -> s.getTotalStock() != null ? s.getTotalStock() : 0).sum();
            int availableStock = stocks.stream().mapToInt(s -> s.getAvailableStock() != null ? s.getAvailableStock() : 0).sum();
            int lockedStock = stocks.stream().mapToInt(s -> s.getLockedStock() != null ? s.getLockedStock() : 0).sum();

            respVO.setTotalStock(totalStock);
            respVO.setAvailableStock(availableStock);
            respVO.setLockedStock(lockedStock);
            respVO.setDeptId(null);
            respVO.setDeptName("全部门店");
        } else {
            AccessoryDeptStockDO stock = accessoryDeptStockMapper.selectByAccessoryIdAndDeptId(
                    accessory.getId(), currentUser.getDeptId());
            if (stock != null) {
                respVO.setTotalStock(stock.getTotalStock());
                respVO.setAvailableStock(stock.getAvailableStock());
                respVO.setLockedStock(stock.getLockedStock());
                respVO.setDeptId(currentUser.getDeptId());

                DeptRespDTO dept = deptApi.getDept(currentUser.getDeptId()).getCheckedData();
                if (dept != null) {
                    respVO.setDeptName(dept.getName());
                }
            } else {
                return null;
            }
        }

        AccessoryWarningDO warning = accessoryWarningMapper.selectByAccessoryId(accessory.getId());
        if (warning != null && (isAdmin || warning.getDeptId().equals(currentUser.getDeptId()))) {
            respVO.setWarningLevel(warning.getLevel());
        }

        return respVO;
    }

    private boolean isAdmin(AdminUserRespDTO user) {
        return user.getDeptId() == null;
    }
}
