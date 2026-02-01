package com.acha.project.controller;

import com.acha.project.common.BaseResponse;
import com.acha.project.model.dto.user.UserRegisterRequestDTO;
import com.acha.project.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@Tag(name = "用户管理", description = "用户注册、登录接口")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 用户注册
     *
     * @param request 注册请求体
     * @return 新用户 ID
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册")
    // 关键点：@Valid 会自动根据 DTO 里的注解进行校验
    // 如果校验不通过，Spring 会自动抛出异常
    public BaseResponse<Long> userRegister(@RequestBody @Valid UserRegisterRequestDTO request) {

        // 1. 调用 Service 层逻辑
        long newUserId = userService.userRegister(
                request.getUserAccount(),
                request.getUserPassword(),
                request.getCheckPassword()
        );

        // 2. 返回统一的响应格式
        return BaseResponse.success(newUserId);
    }
}