package cn.phone.repair.module.repair.dal.dataobject;

import cn.phone.repair.framework.mybatis.core.dataobject.BaseDO;
import cn.phone.repair.framework.tenant.core.aop.TenantIgnore;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@TableName("repair_customer")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TenantIgnore
@ToString(callSuper = true)
public class CustomerDO extends BaseDO {

    @TableId
    private Long id;

    private String name;

    private String mobile;

    private String email;

    private String address;

    private String remark;

}
