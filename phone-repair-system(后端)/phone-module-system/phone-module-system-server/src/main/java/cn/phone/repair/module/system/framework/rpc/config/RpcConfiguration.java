package cn.phone.repair.module.system.framework.rpc.config;

import cn.phone.repair.module.infra.api.config.ConfigApi;
import cn.phone.repair.module.infra.api.file.FileApi;
import cn.phone.repair.module.infra.api.websocket.WebSocketSenderApi;
import cn.phone.repair.module.system.api.user.AdminUserApi;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration(value = "systemRpcConfiguration", proxyBeanMethods = false)
@EnableFeignClients(clients = {FileApi.class, WebSocketSenderApi.class, ConfigApi.class})
public class RpcConfiguration {
}
