package cn.phone.repair.module.repair.framework.datapermission.config;

import cn.phone.repair.framework.datapermission.core.rule.dept.DeptDataPermissionRuleCustomizer;
import cn.phone.repair.module.repair.dal.dataobject.RepairOrderDO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * repair 模块的数据权限 Configuration
 *
 * @author 芋道源码
 */
@Configuration(proxyBeanMethods = false)
public class DataPermissionConfiguration {

    @Bean
    public DeptDataPermissionRuleCustomizer repairDataPermissionRuleCustomizer() {
        return rule -> {
            // dept
            rule.addDeptColumn(RepairOrderDO.class, "dept_id");
            // user
            rule.addUserColumn(RepairOrderDO.class, "staff_id");
        };
    }

}