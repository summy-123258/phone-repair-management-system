package cn.phone.repair.module.repair.dal.dataobject;

import cn.phone.repair.framework.mybatis.core.dataobject.BaseDO;
import cn.phone.repair.framework.tenant.core.aop.TenantIgnore;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 配件预警数据模型
 * 记录配件的库存预警信息，包括预警级别和状态
 */
@TableName("accessory_warning")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TenantIgnore
@ToString(callSuper = true)
public class AccessoryWarningDO extends BaseDO {

    @TableId
    private Long id;

    /**
     * 配件ID
     */
    private Long accessoryId;

    /**
     * 预警级别（normal: 正常, notice: 注意, warning: 警告, severe: 严重）
     */
    private String level;

    /**
     * 当前库存
     */
    private Integer currentStock;

    /**
     * 安全库存
     */
    private Integer safeStock;

    private Long deptId;

    /**
     * 状态（active: 活跃, resolved: 已解决）
     */
    private String status;

    /**
     * 最后通知时间
     */
    private LocalDateTime lastNotifyTime;
}
