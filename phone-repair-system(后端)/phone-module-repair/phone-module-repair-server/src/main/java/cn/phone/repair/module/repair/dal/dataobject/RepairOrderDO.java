package cn.phone.repair.module.repair.dal.dataobject;

import cn.phone.repair.framework.mybatis.core.dataobject.BaseDO;
import cn.phone.repair.framework.tenant.core.aop.TenantIgnore;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("repair_order")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TenantIgnore
@ToString(callSuper = true)
public class RepairOrderDO extends BaseDO {

    @TableId
    private Long id;

    private String orderNo;

    /**
     * 客户ID
     */
    private Long customerId;

    private String phoneModel;

    private String faultDescription;

    private String status;

    private String statusText;

    private BigDecimal cost;

    private LocalDateTime submitTime;

    private LocalDateTime estimatedCompletionTime;

    private LocalDateTime actualCompletionTime;

    private String deliveryOption;

    private String pickupCode;

    /**
     * 负责员工ID
     */
    private Long staffId;

    /**
     * 部门ID（门店ID）
     */
    private Long deptId;
}
