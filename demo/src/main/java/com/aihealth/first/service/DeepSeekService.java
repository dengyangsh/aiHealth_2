package com.aihealth.first.service;

public interface DeepSeekService {

    String getDeepSeekResponse(String systemPrompt, String userPrompt, String questionString, Long userId);
} 