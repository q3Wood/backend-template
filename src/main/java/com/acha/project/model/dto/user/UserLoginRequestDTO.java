package com.acha.project.model.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.io.Serializable;

@Data
@Schema(description = "用户登录请求体")
public class UserLoginRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户账号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "账号不能为空")
    @Size(min = 4, message = "账号长度不能少于 4 位")
    private String userAccount;

    @Schema(description = "用户密码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "密码不能为空")
    @Size(min = 8, message = "密码长度不能少于 8 位")
    private String userPassword;
}