package com.aihealth.first.interceptor;

import com.aihealth.first.annotation.NoAuth;
import com.aihealth.first.entity.UserToken;
import com.aihealth.first.repository.UserTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private UserTokenRepository userTokenRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws IOException {
        // 如果不是映射到方法，直接通过
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;

        // 检查是否有NoAuth注解，有则跳过验证
        if (handlerMethod.getMethod().isAnnotationPresent(NoAuth.class) ||
                handlerMethod.getBeanType().isAnnotationPresent(NoAuth.class)) {
            return true;
        }

        // 从请求头中获取token
        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"success\":false,\"message\":\"未登录或登录已过期\"}");
            return false;
        }
        
        // 处理Bearer前缀
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // 验证token
        Optional<UserToken> userTokenOpt = userTokenRepository.findByToken(token);
        if (!userTokenOpt.isPresent()) {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"success\":false,\"message\":\"无效的token\"}");
            return false;
        }

        UserToken userToken = userTokenOpt.get();

        // 检查token是否过期
        if (userToken.getExpireTime().isBefore(LocalDateTime.now())) {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"success\":false,\"message\":\"登录已过期\"}");
            return false;
        }

        // 将用户ID存入请求属性中，方便后续使用
        request.setAttribute("userId", userToken.getUserId());

        return true;
    }
}