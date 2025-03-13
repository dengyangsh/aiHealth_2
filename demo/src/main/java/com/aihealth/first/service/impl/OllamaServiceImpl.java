package com.aihealth.first.service.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aihealth.first.service.OllamaLogService;
import com.aihealth.first.service.OllamaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.aihealth.first.entity.OllamaRequestLog;

@Service
public class OllamaServiceImpl implements OllamaService {

    private final String url = "http://localhost:11434/api/generate";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String logDirectory = "D:/temp/ollama_logs";
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss.SSS");

    @Autowired
    private OllamaLogService ollamaLogService;

 
    @Override
    public String getPromptResponse(String systemPrompt, String userPrompt, String questionString, Long userId) {
        return getOllamaResponse(systemPrompt, userPrompt, questionString, userId);
    }

    private String getOllamaResponse(String systemPrompt, String userPrompt, String questionString, Long userId) {
        String model = "deepseek-r1:1.5b"; // 当前使用的模型
        Date requestTime = new Date(); // 请求时间
        long startTime = System.currentTimeMillis(); // 开始时间
        String prompt = systemPrompt + userPrompt + "\n用户问题：" + questionString;

        try {
            // 创建URL对象
            URL apiUrl = new URL(url);

            // 打开连接
            HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();

            // 设置请求方法
            connection.setRequestMethod("POST");

            // 设置请求头
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);

            // 构建请求体
            Map<String, String> requestMap = new HashMap<>();
            requestMap.put("model", model);
            requestMap.put("prompt", prompt);

            // 将请求体转换为JSON字符串
            String requestBody = objectMapper.writeValueAsString(requestMap);

            // 发送请求
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // 使用公共方法处理响应
            return processOllamaResponse(connection, prompt, userId, model, questionString, requestTime, startTime);

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
                log.setRequestType("ollamaApi");
                log.setCreatedAt(java.time.LocalDateTime.now());
                log.setOriginalQuestion(""); // 设置空字符串，避免null
                log.setRawResponse(""); // 设置空字符串，避免null
                log.setProcessedResponse(""); // 设置空字符串，避免null
                log.setQuestionAnswer(""); // 设置空字符串，避免null
                ollamaLogService.saveRequestLog(log);
            }

            // 记录错误到文件
            logError(requestTime, model, prompt, e);
            throw new RuntimeException("调用模型时出错: " + e.getMessage(), e);
        }
    }

    public static String truncateByBytes(String str, int maxBytes) {
        if (str == null) {
            return null;
        }
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        if (bytes.length <= maxBytes) {
            return str;
        }
        // 按字节数截断
        byte[] truncatedBytes = Arrays.copyOf(bytes, maxBytes);
        return new String(truncatedBytes, StandardCharsets.UTF_8);
    }

    /**
     * 记录请求和响应
     */
    private void logRequest(Date requestTime, String model, String prompt, String rawResponse, String processedResponse) {
        try {
            // 确保日志目录存在
            File logDir = new File(logDirectory);
            if (!logDir.exists()) {
                logDir.mkdirs();
            }

            // 创建日志文件名（按日期）
            String dateStr = dateFormat.format(requestTime);
            String timeStr = timeFormat.format(requestTime);
            File logFile = new File(logDirectory, "ollama_log_" + dateStr + ".txt");

            // 写入日志
            try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(logFile, true)))) {
                writer.println("=== 请求时间: " + dateStr + " " + timeStr + " ===");
                writer.println("模型: " + model);
                writer.println("--- 请求内容 ---");
                writer.println(prompt);
                writer.println("--- 原始响应 ---");
                writer.println(rawResponse);
                writer.println("--- 处理后响应 ---");
                writer.println(processedResponse);
                writer.println("======================================");
                writer.println();
            }
        } catch (Exception e) {
            System.err.println("记录日志时出错: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 记录错误
     */
    private void logError(Date requestTime, String model, String prompt, Exception error) {
        try {
            // 确保日志目录存在
            File logDir = new File(logDirectory);
            if (!logDir.exists()) {
                logDir.mkdirs();
            }

            // 创建日志文件名（按日期）
            String dateStr = dateFormat.format(requestTime);
            String timeStr = timeFormat.format(requestTime);
            File logFile = new File(logDirectory, "ollama_error_" + dateStr + ".txt");

            // 写入日志
            try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(logFile, true)))) {
                writer.println("=== 错误时间: " + dateStr + " " + timeStr + " ===");
                writer.println("模型: " + model);
                writer.println("--- 请求内容 ---");
                writer.println(prompt);
                writer.println("--- 错误信息 ---");
                error.printStackTrace(writer);
                writer.println("======================================");
                writer.println();
            }
        } catch (Exception e) {
            System.err.println("记录错误日志时出错: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 移除<think>标签及其内容
     */
    private String removeThinkTags(String text) {
        // 使用正则表达式移除<think>标签及其内容
        Pattern pattern = Pattern.compile("<think>.*?</think>", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(text);
        return matcher.replaceAll("");
    }

    /**
     * 移除Markdown代码块标记
     */
    private String removeMarkdownCodeBlocks(String text) {
        // 替换```json和```标记
        text = text.replaceAll("```json", "");
        text = text.replaceAll("```", "");
        return text;
    }

    /**
     * 提取JSON字符串
     */
    private String extractJsonString(String text) {
        // 尝试找到JSON对象的开始和结束
        text = text.trim();

        // 如果文本以{开始并以}结束，则认为是有效的JSON
        if (text.startsWith("{") && text.endsWith("}")) {
            return text;
        }

        // 尝试查找第一个{和最后一个}
        int startIndex = text.indexOf('{');
        int endIndex = text.lastIndexOf('}');

        if (startIndex >= 0 && endIndex > startIndex) {
            return text.substring(startIndex, endIndex + 1);
        }

        // 如果无法提取JSON，则返回原始文本
        return text;
    }

    private String extractQuestionAnswer(String jsonResponse) {
        try {
            Map<String, Object> responseMap = objectMapper.readValue(jsonResponse, Map.class);
            Map<String, Object> questionMap = (Map<String, Object>) responseMap.get("question");
            return (String) questionMap.get("answer");
        } catch (Exception e) {
            System.err.println("解析question.answer时出错: " + e.getMessage());
            return null;
        }
    }

    private String processOllamaResponse(HttpURLConnection connection, String prompt, Long userId, String model, String questionString, Date requestTime, long startTime) throws Exception {
        StringBuilder responseContent = new StringBuilder();
        StringBuilder fullResponseContent = new StringBuilder();
        
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                // 保存原始响应
                fullResponseContent.append(line).append("\n");
                
                // 解析每一行JSON
                Map<String, Object> responseMap = objectMapper.readValue(line, Map.class);
                
                // 检查是否完成
                boolean done = (boolean) responseMap.getOrDefault("done", false);
                
                // 如果有响应内容，添加到结果中
                if (responseMap.containsKey("response")) {
                    String response = (String) responseMap.get("response");
                    responseContent.append(response);
                }
                
                // 如果完成了，跳出循环
                if (done) {
                    break;
                }
            }
        }
        
        // 保存原始响应
        String rawResponse = fullResponseContent.toString();
        
        // 处理响应内容
        String result = responseContent.toString();
        
        // 1. 排除<think>标签的内容
        result = removeThinkTags(result);
        
        // 2. 替换掉```json和```标记
        result = removeMarkdownCodeBlocks(result);
        
        // 3. 处理完成后只保留JSON字符串
        result = extractJsonString(result);
        
        // 保存处理后的响应
        String processedResponse = result;
        
        // 解析处理后的响应，提取question.answer字段
        String questionAnswer = extractQuestionAnswer(processedResponse);
        
        // 计算处理时间
        long endTime = System.currentTimeMillis();
        long processingTime = endTime - startTime;

        // 截断响应内容以适应数据库TEXT类型的限制
        rawResponse = truncateByBytes(rawResponse, 65000);
        processedResponse = truncateByBytes(processedResponse, 65000);
        questionAnswer = truncateByBytes(questionAnswer, 65000);


        // 记录请求和响应到数据库
        if (ollamaLogService != null) {
            OllamaRequestLog log = new OllamaRequestLog();
            log.setUserId(userId);
            log.setModelName(model);
            log.setOriginalQuestion(questionString);
            log.setRawResponse(rawResponse);
            log.setProcessedResponse(processedResponse);
            log.setRequestTime(java.time.LocalDateTime.now());
            log.setResponseTime(java.time.LocalDateTime.now());
            log.setProcessingTimeMs(processingTime);
            log.setStatus("成功");
            log.setQuestionAnswer(questionAnswer);
            log.setRequestType("ollamaApi");
            log.setCreatedAt(java.time.LocalDateTime.now());
            log.setRequestPrompt(prompt);
            ollamaLogService.saveRequestLog(log);
        }

        // 记录请求和响应到文件
        logRequest(requestTime, model, prompt, rawResponse, processedResponse);

        // 返回处理后的响应内容
        return result;
    }
} 