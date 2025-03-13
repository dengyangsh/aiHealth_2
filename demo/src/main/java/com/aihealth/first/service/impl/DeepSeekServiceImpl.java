package com.aihealth.first.service.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aihealth.first.entity.OllamaRequestLog;
import com.aihealth.first.service.DeepSeekService;
import com.aihealth.first.service.OllamaLogService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class DeepSeekServiceImpl implements DeepSeekService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private OllamaLogService ollamaLogService;

    @Override
    public String getDeepSeekResponse(String systemPrompt, String userPrompt, String questionString, Long userId) {
        String model = "deepseek-chat"; // 使用DeepSeek的模型
        Date requestTime = new Date(); // 请求时间
        long startTime = System.currentTimeMillis(); // 开始时间
        String requestType = "deepSeekApi"; // 请求类型标识
        String prompt = userPrompt + "用户问题:" + questionString;
        
        try {
            // 创建URL对象
            URL apiUrl = new URL("https://api.deepseek.com/v1/chat/completions");
            
            // 打开连接
            HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
            
            // 设置请求方法
            connection.setRequestMethod("POST");
            
            // 设置请求头
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer sk-64da1ebd347f48e5941db139ebbf6759"); // 请替换为实际的DeepSeek API Key
            connection.setDoOutput(true);
            connection.setDoInput(true);
            
            // 构建请求体
            Map<String, Object> requestMap = new HashMap<>();
            requestMap.put("model", model);
            requestMap.put("stream", false);
            
            // 创建消息列表
            List<Map<String, String>> messages = new ArrayList<>();
            
            // 添加系统角色信息
            Map<String, String> systemMessage = new HashMap<>();
            systemMessage.put("role", "system");
            systemMessage.put("content", systemPrompt);
            messages.add(systemMessage);
            
            // 获取用户历史对话记录
            List<OllamaRequestLog> historyLogs = ollamaLogService.findRecentByUserIdAndRequestType(userId, requestType, 10);
            
            // 按ID从小到大排序
            historyLogs.sort((a, b) -> a.getId().compareTo(b.getId()));
            
            // 将历史记录添加到消息列表中
            for (OllamaRequestLog log : historyLogs) {
                if (log.getOriginalQuestion() != null && !log.getOriginalQuestion().isEmpty()) {
                    Map<String, String> userMessage = new HashMap<>();
                    userMessage.put("role", "user");
                    userMessage.put("content", log.getOriginalQuestion());
                    messages.add(userMessage);
                }
                
                if (log.getQuestionAnswer() != null && !log.getQuestionAnswer().isEmpty()) {
                    Map<String, String> assistantMessage = new HashMap<>();
                    assistantMessage.put("role", "assistant");
                    assistantMessage.put("content", log.getQuestionAnswer());
                    messages.add(assistantMessage);
                }
            }
            
            // 添加当前用户问题
            Map<String, String> currentUserMessage = new HashMap<>();
            currentUserMessage.put("role", "user");
            currentUserMessage.put("content", prompt);
            messages.add(currentUserMessage);
            
            // 将消息列表添加到请求体
            requestMap.put("messages", messages);
            
            // 将请求体转换为JSON字符串
            String requestBody = objectMapper.writeValueAsString(requestMap);
            
            // 发送请求
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            
            // 获取响应
            int responseCode = connection.getResponseCode();
            
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // 读取响应
                try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder responseBuilder = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        responseBuilder.append(responseLine.trim());
                    }
                    String rawResponse = responseBuilder.toString();
                    
                    // 解析JSON响应
                    JsonNode responseJson = objectMapper.readTree(rawResponse);
                    
                    // 获取模型回复的内容
                    String assistantResponse = "";
                    if (responseJson.has("choices") && responseJson.get("choices").isArray() && responseJson.get("choices").size() > 0) {
                        JsonNode firstChoice = responseJson.get("choices").get(0);
                        if (firstChoice.has("message") && firstChoice.get("message").has("content")) {
                            assistantResponse = firstChoice.get("message").get("content").asText();
                        }
                    }
                    
                    // 计算处理时间
                    long endTime = System.currentTimeMillis();
                    long processingTime = endTime - startTime;
                    
                    // 截断响应内容以适应数据库TEXT类型的限制
                    String truncatedRawResponse = truncateByBytes(rawResponse, 65000);
                    String truncatedAssistantResponse = truncateByBytes(assistantResponse, 65000);
                    
                    // 记录请求和响应到数据库
                    if (ollamaLogService != null) {
                        OllamaRequestLog log = new OllamaRequestLog();
                        log.setUserId(userId);
                        log.setModelName(model);
                        log.setOriginalQuestion(prompt);
                        log.setRawResponse(truncatedRawResponse);
                        log.setProcessedResponse(truncatedAssistantResponse);
                        log.setRequestTime(java.time.LocalDateTime.now());
                        log.setResponseTime(java.time.LocalDateTime.now());
                        log.setProcessingTimeMs(processingTime);
                        log.setStatus("成功");
                        log.setQuestionAnswer(truncatedAssistantResponse);
                        log.setRequestType(requestType);
                        log.setCreatedAt(java.time.LocalDateTime.now());
                        log.setRequestPrompt(prompt);

                        ollamaLogService.saveRequestLog(log);
                    }
                    
                    // 返回处理后的响应内容
                    return assistantResponse;
                }
            } else {
                // 处理错误响应
                try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8))) {
                    StringBuilder errorBuilder = new StringBuilder();
                    String errorLine;
                    while ((errorLine = br.readLine()) != null) {
                        errorBuilder.append(errorLine.trim());
                    }
                    String errorResponse = errorBuilder.toString();
                    
                    throw new RuntimeException("调用DeepSeek API失败，状态码: " + responseCode + ", 错误信息: " + errorResponse);
                }
            }
            
        } catch (Exception e) {
            // 计算处理时间
            long endTime = System.currentTimeMillis();
            long processingTime = endTime - startTime;
            
            // 记录错误到数据库
            if (ollamaLogService != null) {
                OllamaRequestLog log = new OllamaRequestLog();
                log.setUserId(userId);
                log.setModelName(model);
                log.setRequestPrompt(prompt);
                log.setRequestTime(java.time.LocalDateTime.now());
                log.setResponseTime(java.time.LocalDateTime.now());
                log.setProcessingTimeMs(processingTime);
                log.setStatus("失败");
                log.setErrorMessage(e.getMessage() != null ? e.getMessage() : "未知错误");
                log.setRequestType(requestType);
                log.setCreatedAt(java.time.LocalDateTime.now());
                log.setOriginalQuestion(""); // 设置空字符串，避免null
                log.setRawResponse(""); // 设置空字符串，避免null
                log.setProcessedResponse(""); // 设置空字符串，避免null
                log.setQuestionAnswer(""); // 设置空字符串，避免null
                ollamaLogService.saveRequestLog(log);
            }
            
            // 记录错误到文件
            logError(requestTime, model, prompt, e);
            throw new RuntimeException("调用DeepSeek模型时出错: " + e.getMessage(), e);
        }
    }


    /**
     * 记录错误
     */
    private void logError(Date requestTime, String model, String prompt, Exception error) {
        // 这里可以实现错误日志记录逻辑，类似于OllamaServiceImpl中的方法
        System.err.println("=== 错误时间: " + requestTime + " ===");
        System.err.println("模型: " + model);
        System.err.println("--- 请求内容 ---");
        System.err.println(prompt);
        System.err.println("--- 错误信息 ---");
        error.printStackTrace(System.err);
    }

    /**
     * 按字节数截断字符串
     */
    private String truncateByBytes(String str, int maxBytes) {
        if (str == null) {
            return null;
        }
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        if (bytes.length <= maxBytes) {
            return str;
        }
        // 按字节数截断
        byte[] truncatedBytes = new byte[maxBytes];
        System.arraycopy(bytes, 0, truncatedBytes, 0, maxBytes);
        return new String(truncatedBytes, StandardCharsets.UTF_8);
    }
} 