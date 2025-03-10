package com.aihealth.first.service;

import com.aihealth.first.entity.FitnessCycle;
import com.aihealth.first.entity.User;
import com.aihealth.first.repository.FitnessCycleRepository;
import com.aihealth.first.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class InquiryService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FitnessCycleRepository fitnessCycleRepository;

    @Autowired
    private OllamaService ollamaService;

    public String handleInquiry(Long userId, String question) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Optional<FitnessCycle> activeCycle = fitnessCycleRepository.findByUserIdAndStatus(userId, FitnessCycle.CycleStatus.ACTIVE);

            if (activeCycle.isPresent()) {
                FitnessCycle cycle = activeCycle.get();
                // 将用户的健身周期数据和问题传递给模型
                Map<String, Object> requestData = new HashMap<>();
                requestData.put("user", user);
                requestData.put("cycle", cycle);
                requestData.put("question", question);
                return ollamaService.getAdviceFromOllama(requestData).toString();
            } else {
                // 没有活跃的健身周期，直接返回模型的回答
                Map<String, Object> requestData = new HashMap<>();
                requestData.put("user", user);
                requestData.put("question", question);
                return ollamaService.getAdviceFromOllama(requestData).toString();
            }
        }
        throw new RuntimeException("User not found");
    }

    public String handleModification(Long userId, String modificationRequest) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Optional<FitnessCycle> activeCycle = fitnessCycleRepository.findByUserIdAndStatus(userId, FitnessCycle.CycleStatus.ACTIVE);

            if (activeCycle.isPresent()) {
                FitnessCycle cycle = activeCycle.get();
                // 将用户的健身周期数据和修改请求传递给模型
                Map<String, Object> requestData = new HashMap<>();
                requestData.put("user", user);
                requestData.put("cycle", cycle);
                requestData.put("modificationRequest", modificationRequest);
                return ollamaService.getAdviceFromOllama(requestData).toString();
            } else {
                throw new RuntimeException("No active fitness cycle found for user");
            }
        }
        throw new RuntimeException("User not found");
    }
} 