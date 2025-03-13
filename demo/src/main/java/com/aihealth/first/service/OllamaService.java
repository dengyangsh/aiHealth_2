package com.aihealth.first.service;

public interface OllamaService {


    String getPromptResponse(String systemPrompt, String userPrompt, String questionString, Long userId);

    // 其他方法签名
}
