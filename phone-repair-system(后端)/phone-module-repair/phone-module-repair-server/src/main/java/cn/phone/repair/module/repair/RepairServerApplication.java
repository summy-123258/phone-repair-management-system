package cn.phone.repair.module.repair;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * 项目的启动类
 *
 * @author 芋道源码
 */
@SpringBootApplication
@EnableFeignClients(basePackages = {"cn.phone.repair.module.system.framework.rpc.config"})
public class RepairServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(RepairServerApplication.class, args);
    }
}
