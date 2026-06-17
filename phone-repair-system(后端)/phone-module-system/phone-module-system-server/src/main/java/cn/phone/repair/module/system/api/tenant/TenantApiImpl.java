package cn.phone.repair.module.system.api.tenant;

import cn.phone.repair.framework.common.biz.system.tenant.TenantCommonApi;
import cn.phone.repair.framework.common.pojo.CommonResult;
import cn.phone.repair.framework.tenant.core.aop.TenantIgnore;
import cn.phone.repair.module.system.service.tenant.TenantService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

import static cn.phone.repair.framework.common.pojo.CommonResult.success;

@RestController // 提供 RESTful API 接口，给 Feign 调用
@Validated
public class TenantApiImpl implements TenantCommonApi {

    @Resource
    private TenantService tenantService;

    @Override
    @TenantIgnore // 防止递归。避免调用 /rpc-api/system/tenant/valid 接口时，又去触发 /rpc-api/system/tenant/valid 去校验
    public CommonResult<List<Long>> getTenantIdList() {
        return success(tenantService.getTenantIdList());
    }

    @Override
    @TenantIgnore // 获得租户列表的时候，无需传递租户编号
    public CommonResult<Boolean> validTenant(Long id) {
        tenantService.validTenant(id);
        return success(true);
    }

}
