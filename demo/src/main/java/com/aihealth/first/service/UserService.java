package com.aihealth.first.service;

import com.aihealth.first.entity.User;
import com.aihealth.first.entity.FitnessCycle;
import com.aihealth.first.entity.CycleDiet;
import com.aihealth.first.entity.CycleExercise;
import com.aihealth.first.repository.UserRepository;
import com.aihealth.first.repository.FitnessCycleRepository;
import com.aihealth.first.repository.CycleDietRepository;
import com.aihealth.first.repository.CycleExerciseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.aihealth.first.service.OllamaService;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FitnessCycleRepository fitnessCycleRepository;

    @Autowired
    private CycleDietRepository cycleDietRepository;

    @Autowired
    private CycleExerciseRepository cycleExerciseRepository;

    @Autowired
    private OllamaService ollamaService;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    public User bindDevice(Long userId, String scaleDeviceId, String speakerDeviceId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setScaleDeviceId(scaleDeviceId);
            user.setSpeakerDeviceId(speakerDeviceId);
            return userRepository.save(user);
        }
        throw new RuntimeException("User not found");
    }

    public User uploadUserData(Long userId, Double height, Double weight, Double bodyFatRate) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setHeight(height);
            user.setWeight(weight);
            user.setBodyFatRate(bodyFatRate);
            return userRepository.save(user);
        }
        throw new RuntimeException("User not found");
    }

    public User updateUserData(Long userId, Integer age, Double height, Double weight, Double bodyFatRate) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setAge(age);
            user.setHeight(height);
            user.setWeight(weight);
            user.setBodyFatRate(bodyFatRate);
            return userRepository.save(user);
        }
        throw new RuntimeException("User not found");
    }

    public User updateUserDataFromSpeaker(Long userId, String speakerInput) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Map<String, Double> updates = analyzeSpeakerInput(speakerInput);

            if (updates.containsKey("height")) {
                user.setHeight(updates.get("height"));
            }
            if (updates.containsKey("weight")) {
                user.setWeight(updates.get("weight"));
            }
            if (updates.containsKey("bodyFatRate")) {
                user.setBodyFatRate(updates.get("bodyFatRate"));
            }

            return userRepository.save(user);
        }
        throw new RuntimeException("User not found");
    }

    private Map<String, Double> analyzeSpeakerInput(String speakerInput) {
        // 假设这是一个分析语音输入的复杂逻辑
        // 假设我们有一个ollama模型的客户端
        String ollamaApiUrl = "http://localhost:11434/api/analyze";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("input", speakerInput);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(ollamaApiUrl, request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                Map<String, Double> updates = objectMapper.readValue(response.getBody(), new TypeReference<Map<String, Double>>() {});
                return updates;
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to parse response from ollama", e);
            }
        } else {
            throw new RuntimeException("Failed to get response from ollama");
        }
        // 返回一个包含需要更新的数据项的Map
        // 例如：{"height": 180.0, "weight": 75.0}
        // return new HashMap<>(); // 这里需要实现具体的分析逻辑
    }

    public FitnessCycle createFitnessCycle(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Map<String, Object> userData = new HashMap<>();
            userData.put("height", user.getHeight());
            userData.put("weight", user.getWeight());
            userData.put("bodyFatRate", user.getBodyFatRate());

            Map<String, Object> advice = ollamaService.getAdviceFromOllama(userData);

            FitnessCycle fitnessCycle = new FitnessCycle();
            fitnessCycle.setUserId(userId);
            fitnessCycle.setCycleNumber("Cycle-" + System.currentTimeMillis());
            fitnessCycle.setStatus(FitnessCycle.CycleStatus.PLANNED);
            fitnessCycle.setGoal((String) advice.get("goal"));
            fitnessCycle.setStartWeight(user.getWeight());
            fitnessCycle.setStartTime(LocalDateTime.now());

            // 解析ollama返回的饮食和运动计划，入库
            // 解析饮食计划
            if (advice.containsKey("cycleDiet")) {
                Map<String, Object> dietPlan = (Map<String, Object>) advice.get("cycleDiet");
                CycleDiet cycleDiet = new CycleDiet();
                cycleDiet.setCycleId(fitnessCycle.getId());
                cycleDiet.setDietContent((String) dietPlan.get("content"));
                cycleDiet.setCaloriesIntake((Double) dietPlan.get("calories")); 
                cycleDiet.setDietDescription((String) dietPlan.get("description"));
                cycleDietRepository.save(cycleDiet);
            }

            // 解析运动计划
            if (advice.containsKey("cycleExercise")) {
                Map<String, Object> exercisePlan = (Map<String, Object>) advice.get("cycleExercise");
                CycleExercise cycleExercise = new CycleExercise();
                cycleExercise.setCycleId(fitnessCycle.getId());
                cycleExercise.setExerciseDate(LocalDate.now());
                cycleExercise.setExerciseTime(LocalTime.now());
                cycleExercise.setExerciseContent((String) exercisePlan.get("content"));
                cycleExercise.setDurationMinutes((Integer) exercisePlan.get("duration"));
                cycleExercise.setCaloriesBurned((Double) exercisePlan.get("calories"));
                cycleExercise.setExerciseDescription((String) exercisePlan.get("description"));
                cycleExerciseRepository.save(cycleExercise);
            }

            return fitnessCycleRepository.save(fitnessCycle);
        }
        throw new RuntimeException("User not found");
    }

    // 用户APP修改健身周期数据的逻辑
    public FitnessCycle updateFitnessCycle(Long cycleId, FitnessCycle updatedCycle) {
        Optional<FitnessCycle> cycleOptional = fitnessCycleRepository.findById(cycleId);
        if (cycleOptional.isPresent()) {
            FitnessCycle existingCycle = cycleOptional.get();
            existingCycle.setGoal(updatedCycle.getGoal());
            existingCycle.setStatus(updatedCycle.getStatus());
            existingCycle.setEndWeight(updatedCycle.getEndWeight());
            existingCycle.setEndTime(updatedCycle.getEndTime());
            return fitnessCycleRepository.save(existingCycle);
        }
        throw new RuntimeException("Fitness cycle not found");
    }
}