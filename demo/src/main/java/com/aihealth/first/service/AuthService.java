package com.aihealth.first.service;

import com.aihealth.first.domain.LoginDomain;
import com.aihealth.first.domain.LoginResponseDomain;
import com.aihealth.first.entity.UserToken;

/**
 * 认证服务接口
 */
public interface AuthService {
    
    /**
     * 处理用户登录
     * @param loginDomain 登录信息
     * @return 登录响应
     */
    LoginResponseDomain login(LoginDomain loginDomain);
    
    /**
     * 检查用户名是否已存在
     * @param username 用户名
     * @return 是否存在
     */
    boolean isUsernameExists(String username);
    
    /**
     * 生成随机token
     * @return token字符串
     */
    String generateToken();
    
    /**
     * 保存或更新用户token
     * @param userId 用户ID
     * @param token 令牌
     * @return 保存的token
     */
    UserToken saveOrUpdateToken(Long userId, String token);
    
    /**
     * 验证token是否有效
     * @param token 令牌
     * @return 是否有效
     */
    boolean validateToken(String token);
    
    /**
     * 根据token获取用户ID
     * @param token 令牌
     * @return 用户ID
     */
    Long getUserIdByToken(String token);
    
    /**
     * 用户登出
     * @param userId 用户ID
     */
    void logout(Long userId);
} 