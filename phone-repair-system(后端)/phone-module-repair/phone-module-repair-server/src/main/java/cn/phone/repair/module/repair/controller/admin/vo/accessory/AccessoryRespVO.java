package cn.phone.repair.module.repair.controller.admin.vo.accessory;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 配件响应视图对象
 * 用于返回配件的详细信息，包括基本信息、库存信息、预警级别和部门信息
 */
@Data
public class AccessoryRespVO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 配件名称
     */
    private String name;

    /**
     * 规格型号
     */
    private String spec;

    /**
     * 单位
     */
    private String unit;

    /**
     * 安全库存阈值
     */
    private Integer safeStock;

    /**
     * 最大库存上限
     */
    private Integer maxStock;

    /**
     * 供应商
     */
    private String supplier;

    /**
     * 备注
     */
    private String remark;

    /**
     * 总库存数量
     */
    private Integer totalStock;

    /**
     * 可用库存数量
     */
    private Integer availableStock;

    /**
     * 锁定库存数量
     */
    private Integer lockedStock;

    /**
     * 预警级别（normal、notice、warning、severe）
     */
    private String warningLevel;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 更新人
     */
    private String updater;

    /**
     * 部门ID(门店id)
     */
    private Long deptId;

    /**
     * 部门名称(门店名称)
     */
    private String deptName;
}