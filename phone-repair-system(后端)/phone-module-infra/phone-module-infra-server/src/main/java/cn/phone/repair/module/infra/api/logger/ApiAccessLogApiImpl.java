package cn.phone.repair.module.infra.api.logger;

import cn.phone.repair.framework.common.biz.infra.logger.ApiAccessLogCommonApi;
import cn.phone.repair.framework.common.biz.infra.logger.dto.ApiAccessLogCreateReqDTO;
import cn.phone.repair.framework.common.pojo.CommonResult;
import cn.phone.repair.module.infra.service.logger.ApiAccessLogService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

import static cn.phone.repair.framework.common.pojo.CommonResult.success;

@RestController // 提供 RESTful API 接口，给 Feign 调用
@Validated
public class ApiAccessLogApiImpl implements ApiAccessLogCommonApi {

    @Resource
    private ApiAccessLogService apiAccessLogService;

    @Override
    public CommonResult<Boolean> createApiAccessLog(ApiAccessLogCreateReqDTO createDTO) {
        apiAccessLogService.createApiAccessLog(createDTO);
        return success(true);
    }

}
