package cn.phone.repair.module.system.api.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "RPC 服务 - 用户创建/修改 Request DTO")
@Data
public class AdminUserSaveReqDTO {

    @Schema(description = "用户编号", example = "1024")
    private Long id;

    @Schema(description = "用户账号", example = "phone13800138000")
    private String username;

    @Schema(description = "用户昵称", example = "客户")
    private String nickname;

    @Schema(description = "手机号码", example = "13800138000")
    private String mobile;

    @Schema(description = "密码", example = "123456")
    private String password;

    @Schema(description = "状态", example = "1")
    private Integer status;
}
