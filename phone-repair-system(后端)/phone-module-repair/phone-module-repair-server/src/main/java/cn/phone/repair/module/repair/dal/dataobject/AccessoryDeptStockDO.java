package cn.phone.repair.module.repair.dal.dataobject;

import cn.phone.repair.framework.mybatis.core.dataobject.BaseDO;
import cn.phone.repair.framework.tenant.core.aop.TenantIgnore;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@TableName("accessory_dept_stock")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TenantIgnore
@ToString(callSuper = true)
public class AccessoryDeptStockDO extends BaseDO {

    @TableId
    private Long id;

    private Long accessoryId;

    private Long deptId;

    private Integer totalStock;

    private Integer availableStock;

    private Integer lockedStock;
}