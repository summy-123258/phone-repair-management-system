package cn.phone.repair.module.repair.dal.dataobject;

import cn.phone.repair.framework.mybatis.core.dataobject.BaseDO;
import cn.phone.repair.framework.tenant.core.aop.TenantIgnore;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

@TableName("repair_progress")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TenantIgnore
public class RepairProgressDO extends BaseDO {

    @TableId
    private Long id;

    private Long orderId;

    private String status;

    private String statusText;

    private String description;

    private LocalDateTime time;
}
