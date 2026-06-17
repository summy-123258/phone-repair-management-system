package cn.phone.repair.module.repair.dal.dataobject;

import cn.phone.repair.framework.mybatis.core.dataobject.BaseDO;
import cn.phone.repair.framework.tenant.core.aop.TenantIgnore;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 配件流水数据模型
 * 记录配件的领用和退库操作，关联维修订单
 */
@TableName("accessory_flow")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TenantIgnore
@ToString(callSuper = true)
public class AccessoryFlowDO extends BaseDO {

    @TableId
    private Long id;

    /**
     * 配件ID
     */
    private Long accessoryId;

    /**
     * 维修单号
     */
    private String repairOrderNo;

    /**
     * 操作类型（use: 领用, return: 退库）
     */
    private String type;

    /**
     * 数量
     */
    private Integer quantity;

    /**
     * 操作人ID
     */
    private Long operatorId;

    /**
     * 操作人名称
     */
    private String operator;

    /**
     * 所属门店ID
     */
    private Long deptId;

    /**
     * 原因（退库时必填）
     */
    private String reason;

    /**
     * 备注
     */
    private String remark;
}
