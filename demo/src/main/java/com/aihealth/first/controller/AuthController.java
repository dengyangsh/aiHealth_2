package com.aihealth.first.controller;

import com.aihealth.first.annotation.NoAuth;
import com.aihealth.first.domain.LoginDomain;
import com.aihealth.first.domain.LoginResponseDomain;
import com.aihealth.first.domain.UserDomain;
import com.aihealth.first.entity.User;
import com.aihealth.first.service.AuthService;
import com.aihealth.first.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;
    
    @Autowired
    private UserService userService;    

    /**
     * 用户登录接口
     * @param loginDomain 登录信息
     * @return 登录响应
     */
    @NoAuth
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDomain> login(@Valid @RequestBody LoginDomain loginDomain) {
        LoginResponseDomain response = authService.login(loginDomain);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 用户注册接口
     * @param userDomain 用户信息
     * @return 注册响应
     */
    @NoAuth
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserDomain userDomain) {
        try {
            // 检查用户名是否已存在
            if (authService.isUsernameExists(userDomain.getUsername())) {
                LoginResponseDomain response = new LoginResponseDomain();
                response.setSuccess(false);
                response.setMessage("用户名已存在");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            
            // 创建新用户
            User user = new User();
            user.setUsername(userDomain.getUsername());
            user.setPassword(userDomain.getPassword());
            user.setAge(userDomain.getAge());
            user.setHeight(userDomain.getHeight());
            user.setWeight(userDomain.getWeight());
            user.setBodyFatRate(userDomain.getBodyFatRate());
            user.setScaleDeviceId(userDomain.getScaleDeviceId());
            user.setSpeakerDeviceId(userDomain.getSpeakerDeviceId());
            
            User savedUser = userService.save(user);
            
            // 创建注册成功响应
            LoginResponseDomain response = new LoginResponseDomain();
            response.setUserId(savedUser.getId());
            response.setUsername(savedUser.getUsername());
            response.setToken(authService.generateToken());
            response.setSuccess(true);
            response.setMessage("注册成功");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            LoginResponseDomain response = new LoginResponseDomain();
            response.setSuccess(false);
            response.setMessage("注册失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 用户登出接口
     * @param request HTTP请求
     * @return 登出响应
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        authService.logout(userId);
        
        LoginResponseDomain response = new LoginResponseDomain();
        response.setSuccess(true);
        response.setMessage("登出成功");
        
        return ResponseEntity.ok(response);
    }
} 