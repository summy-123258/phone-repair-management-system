package cn.phone.repair.module.repair.service.impl;

import cn.phone.repair.framework.common.util.object.BeanUtils;
import cn.phone.repair.framework.security.core.util.SecurityFrameworkUtils;
import cn.phone.repair.module.repair.controller.admin.vo.accessory.AccessoryWarningRespVO;
import cn.phone.repair.module.repair.dal.dataobject.AccessoryDO;
import cn.phone.repair.module.repair.dal.dataobject.AccessoryDeptStockDO;
import cn.phone.repair.module.repair.dal.dataobject.AccessoryWarningDO;
import cn.phone.repair.module.repair.dal.mysql.AccessoryDeptStockMapper;
import cn.phone.repair.module.repair.dal.mysql.AccessoryMapper;
import cn.phone.repair.module.repair.dal.mysql.AccessoryWarningMapper;
import cn.phone.repair.module.repair.service.AccessoryService;
import cn.phone.repair.module.repair.service.AccessoryWarningService;
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

@Service
@Slf4j
public class AccessoryWarningServiceImpl implements AccessoryWarningService {

    @Resource
    private AccessoryWarningMapper accessoryWarningMapper;

    @Resource
    private AccessoryMapper accessoryMapper;

    @Resource
    private AccessoryDeptStockMapper accessoryDeptStockMapper;

    @Resource
    private AccessoryService accessoryService;

    @Resource
    private DeptApi deptApi;

    @Resource
    private AdminUserApi adminUserApi;

    @Override
    public List<AccessoryWarningRespVO> getAccessoryWarningList() {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        AdminUserRespDTO user = adminUserApi.getUser(userId).getCheckedData();
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        List<AccessoryWarningDO> warnings;
        if (isAdmin(userId)) {
            warnings = accessoryWarningMapper.selectListByStatus("active");
        } else {
            warnings = accessoryWarningMapper.selectList();
            warnings.removeIf(warning -> !user.getDeptId().equals(warning.getDeptId()));
        }

        return buildAccessoryWarningRespVOList(warnings);
    }

    @Override
    public List<AccessoryWarningRespVO> getAccessoryWarningListByStatus(String status) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        AdminUserRespDTO user = adminUserApi.getUser(userId).getCheckedData();
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        List<AccessoryWarningDO> warnings;
        if (isAdmin(userId)) {
            warnings = accessoryWarningMapper.selectListByStatus(status);
        } else {
            warnings = accessoryWarningMapper.selectListByStatus(status);
            warnings.removeIf(warning -> !user.getDeptId().equals(warning.getDeptId()));
        }

        return buildAccessoryWarningRespVOList(warnings);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateWarningStatus(Long id, String status) {
        AccessoryWarningDO warning = accessoryWarningMapper.selectById(id);
        if (warning == null) {
            throw new RuntimeException("预警记录不存在");
        }

        Long userId = SecurityFrameworkUtils.getLoginUserId();
        AdminUserRespDTO user = adminUserApi.getUser(userId).getCheckedData();
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        if (!isAdmin(userId) && !warning.getDeptId().equals(user.getDeptId())) {
            throw new RuntimeException("无权操作其他部门的配件预警");
        }

        warning.setStatus(status);
        warning.setUpdateTime(LocalDateTime.now());
        accessoryWarningMapper.updateById(warning);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void checkAllAccessoryWarnings() {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        AdminUserRespDTO user = adminUserApi.getUser(userId).getCheckedData();
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        List<AccessoryDO> accessories = accessoryMapper.selectList();

        for (AccessoryDO accessory : accessories) {
            if (isAdmin(userId)) {
                List<AccessoryDeptStockDO> stocks = accessoryDeptStockMapper.selectListByAccessoryId(accessory.getId());
                for (AccessoryDeptStockDO stock : stocks) {
                    accessoryService.checkAndUpdateWarningStatus(accessory.getId(), stock.getDeptId());
                }
            } else {
                AccessoryDeptStockDO stock = accessoryDeptStockMapper.selectByAccessoryIdAndDeptId(
                        accessory.getId(), user.getDeptId());
                if (stock != null) {
                    accessoryService.checkAndUpdateWarningStatus(accessory.getId(), user.getDeptId());
                }
            }
        }
    }

    private boolean isAdmin(Long userId) {
        return SecurityFrameworkUtils.getLoginUserDeptId() == null;
    }

    private List<AccessoryWarningRespVO> buildAccessoryWarningRespVOList(List<AccessoryWarningDO> warnings) {
        List<AccessoryWarningRespVO> respVOs = new ArrayList<>();
        for (AccessoryWarningDO warning : warnings) {
            AccessoryWarningRespVO respVO = BeanUtils.toBean(warning, AccessoryWarningRespVO.class);
            if (respVO == null) {
                continue;
            }

            AccessoryDO accessory = accessoryMapper.selectById(warning.getAccessoryId());
            if (accessory != null) {
                respVO.setAccessoryName(accessory.getName());
                respVO.setAccessorySpec(accessory.getSpec());
            }

            if (warning.getDeptId() != null) {
                DeptRespDTO dept = deptApi.getDept(warning.getDeptId()).getCheckedData();
                if (dept != null) {
                    respVO.setDeptName(dept.getName());
                }
            }

            respVOs.add(respVO);
        }
        return respVOs;
    }
}
