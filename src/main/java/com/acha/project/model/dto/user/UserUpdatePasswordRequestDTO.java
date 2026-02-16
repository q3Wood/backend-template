package com.acha.project.model.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.io.Serializable;

@Data
@Schema(description = "用户修改密码请求体")
public class UserUpdatePasswordRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "旧密码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "旧密码不能为空")
    private String oldPassword;

    @Schema(description = "新密码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "新密码不能为空")
    @Size(min = 8, message = "新密码长度不能少于 8 位")
    private String newPassword;

    @Schema(description = "确认新密码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "确认新密码不能为空")
    private String checkPassword;
}
