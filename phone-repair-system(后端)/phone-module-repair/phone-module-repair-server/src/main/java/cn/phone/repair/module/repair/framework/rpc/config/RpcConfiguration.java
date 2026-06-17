package cn.phone.repair.module.repair.framework.rpc.config;

import cn.phone.repair.module.system.api.dept.DeptApi;
import cn.phone.repair.module.system.api.user.AdminUserApi;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableFeignClients(clients = {AdminUserApi.class, DeptApi.class})
public class RpcConfiguration {
}
