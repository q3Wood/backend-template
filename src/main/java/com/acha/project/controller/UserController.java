package com.acha.project.controller;

import com.acha.project.common.BaseResponse;
import com.acha.project.model.dto.user.UserLoginRequestDTO;
import com.acha.project.model.dto.user.UserRegisterRequestDTO;
import com.acha.project.model.vo.user.UserVO;
import com.acha.project.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

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

    /**
     * 用户登录
     *
     * @param request       登录请求参数
     * @param httpServletRequest 请求上下文 (用来存 Session)
     * @return 脱敏后的用户信息
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public BaseResponse<UserVO> userLogin(@RequestBody @Valid UserLoginRequestDTO request, HttpServletRequest httpServletRequest) {

        // 1. 检查参数 (Controller 层只做非空检查，Service 层做业务检查)
        if (request == null) {
            throw new RuntimeException("请求参数为空");
        }

        String userAccount = request.getUserAccount();
        String userPassword = request.getUserPassword();

        // 2. 调用 Service
        UserVO userVO = userService.userLogin(userAccount, userPassword, httpServletRequest);

        // 3. 返回成功
        return BaseResponse.success(userVO);
    }

    /**
     * 获取当前登录用户
     *
     * @return 这里的 UserVO 是从数据库查出来的最新版
     */
    @GetMapping("/current")
    @Operation(summary = "获取当前登录用户")
    public BaseResponse<UserVO> getLoginUser() {
        UserVO userVO = userService.getLoginUser();
        return BaseResponse.success(userVO);
    }

    /**
     * 用户注销
     *
     * @param request 请求上下文
     * @return 成功
     */
    @PostMapping("/logout")
    @Operation(summary = "用户注销")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new RuntimeException("请求参数为空");
        }
        boolean result = userService.userLogout(request);
        return BaseResponse.success(result);
    }


}