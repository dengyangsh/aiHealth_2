package com.aihealth.first.service.impl;

import com.aihealth.first.domain.LoginDomain;
import com.aihealth.first.domain.LoginResponseDomain;
import com.aihealth.first.entity.User;
import com.aihealth.first.entity.UserToken;
import com.aihealth.first.repository.UserRepository;
import com.aihealth.first.repository.UserTokenRepository;
import com.aihealth.first.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserTokenRepository userTokenRepository;

    /**
     * 处理用户登录
     * @param loginDomain 登录信息
     * @return 登录响应
     */
    @Override
    @Transactional
    public LoginResponseDomain login(LoginDomain loginDomain) {
        LoginResponseDomain response = new LoginResponseDomain();
        
        // 根据用户名查找用户
        Optional<User> userOpt = userRepository.findByUsername(loginDomain.getUsername());
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            
            // 验证密码
            if (user.getPassword().equals(loginDomain.getPassword())) {
                // 登录成功，生成token
                String token = generateToken();
                
                // 保存或更新token
                saveOrUpdateToken(user.getId(), token);
                
                response.setUserId(user.getId());
                response.setUsername(user.getUsername());
                response.setToken(token);
                response.setSuccess(true);
                response.setMessage("登录成功");
            } else {
                // 密码错误
                response.setSuccess(false);
                response.setMessage("密码错误");
            }
        } else {
            // 用户不存在
            response.setSuccess(false);
            response.setMessage("用户不存在");
        }
        
        return response;
    }
    
    /**
     * 检查用户名是否已存在
     * @param username 用户名
     * @return 是否存在
     */
    @Override
    public boolean isUsernameExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }
    
    /**
     * 生成随机token
     * @return token字符串
     */
    @Override
    public String generateToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }
    
    /**
     * 保存或更新用户token
     * @param userId 用户ID
     * @param token 令牌
     * @return 保存的token
     */
    @Override
    @Transactional
    public UserToken saveOrUpdateToken(Long userId, String token) {
        // 查找用户是否已有token
        Optional<UserToken> userTokenOpt = userTokenRepository.findByUserId(userId);
        
        UserToken userToken;
        if (userTokenOpt.isPresent()) {
            // 更新已有token
            userToken = userTokenOpt.get();
            userToken.setToken(token);
            userToken.setExpireTime(LocalDateTime.now().plusDays(7)); // token有效期7天
        } else {
            // 创建新token
            userToken = new UserToken();
            userToken.setUserId(userId);
            userToken.setToken(token);
            userToken.setExpireTime(LocalDateTime.now().plusDays(7)); // token有效期7天
        }
        
        return userTokenRepository.save(userToken);
    }
    
    /**
     * 验证token是否有效
     * @param token 令牌
     * @return 是否有效
     */
    @Override
    public boolean validateToken(String token) {
        Optional<UserToken> userTokenOpt = userTokenRepository.findByToken(token);
        if (!userTokenOpt.isPresent()) {
            return false;
        }
        
        UserToken userToken = userTokenOpt.get();
        return userToken.getExpireTime().isAfter(LocalDateTime.now());
    }
    
    /**
     * 根据token获取用户ID
     * @param token 令牌
     * @return 用户ID
     */
    @Override
    public Long getUserIdByToken(String token) {
        Optional<UserToken> userTokenOpt = userTokenRepository.findByToken(token);
        if (!userTokenOpt.isPresent()) {
            throw new RuntimeException("无效的token");
        }
        
        return userTokenOpt.get().getUserId();
    }
    
    /**
     * 用户登出
     * @param userId 用户ID
     */
    @Override
    @Transactional
    public void logout(Long userId) {
        userTokenRepository.deleteByUserId(userId);
    }
} 