package com.aihealth.first.service.impl;

import com.aihealth.first.entity.OllamaRequestLog;
import com.aihealth.first.repository.OllamaRequestLogRepository;
import com.aihealth.first.service.OllamaLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OllamaLogServiceImpl implements OllamaLogService {

    @Autowired
    private OllamaRequestLogRepository ollamaRequestLogRepository;

    @Override
    public OllamaRequestLog saveRequestLog(OllamaRequestLog ollamaRequestLog) {
        return ollamaRequestLogRepository.save(ollamaRequestLog);
    }

    @Override
    public OllamaRequestLog createRequestLog(Long userId, String modelName, String originalQuestion, 
                                           String rawResponse, String processedResponse, 
                                           long processingTimeMs, String status, String questionAnswer) {
        OllamaRequestLog log = new OllamaRequestLog();
        log.setUserId(userId);
        log.setModelName(modelName);
        log.setOriginalQuestion(originalQuestion != null ? originalQuestion : "");
        log.setRawResponse(rawResponse != null ? rawResponse : "");
        log.setProcessedResponse(processedResponse != null ? processedResponse : "");
        log.setRequestTime(LocalDateTime.now());
        log.setResponseTime(LocalDateTime.now());
        log.setProcessingTimeMs(processingTimeMs);
        log.setStatus(status != null ? status : "成功");
        log.setQuestionAnswer(questionAnswer != null ? questionAnswer : "");
        log.setCreatedAt(LocalDateTime.now());
        log.setRequestPrompt(originalQuestion != null ? originalQuestion : "");
        log.setRequestType("ollamaApi");
        log.setErrorMessage("");
        
        return saveRequestLog(log);
    }

    @Override
    public OllamaRequestLog createErrorLog(Long userId, String modelName, String requestPrompt, String errorMessage) {
        OllamaRequestLog log = new OllamaRequestLog();
        log.setUserId(userId);
        log.setModelName(modelName);
        log.setRequestPrompt(requestPrompt);
        log.setRequestTime(LocalDateTime.now());
        log.setResponseTime(LocalDateTime.now());
        log.setStatus("失败");
        log.setErrorMessage(errorMessage);
        log.setCreatedAt(LocalDateTime.now());
        log.setProcessingTimeMs(0L);
        log.setOriginalQuestion("");
        log.setRawResponse("");
        log.setProcessedResponse("");
        log.setQuestionAnswer("");
        log.setRequestType("unknown");
        
        return saveRequestLog(log);
    }

    @Override
    public Optional<OllamaRequestLog> findById(Long id) {
        return ollamaRequestLogRepository.findById(id);
    }

    @Override
    public List<OllamaRequestLog> findAll() {
        return ollamaRequestLogRepository.findAll();
    }

    @Override
    public List<OllamaRequestLog> findByUserId(Long userId) {
        return ollamaRequestLogRepository.findByUserId(userId);
    }

    @Override
    public Page<OllamaRequestLog> findByUserId(Long userId, Pageable pageable) {
        return ollamaRequestLogRepository.findByUserId(userId, pageable);
    }

    @Override
    public List<OllamaRequestLog> findByModelName(String modelName) {
        return ollamaRequestLogRepository.findByModelName(modelName);
    }

    @Override
    public List<OllamaRequestLog> findByRequestTimeBetween(LocalDateTime startTime, LocalDateTime endTime) {
        return ollamaRequestLogRepository.findByRequestTimeBetween(startTime, endTime);
    }

    @Override
    public List<OllamaRequestLog> findByUserIdAndRequestTimeBetween(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        return ollamaRequestLogRepository.findByUserIdAndRequestTimeBetween(userId, startTime, endTime);
    }

    @Override
    public List<OllamaRequestLog> findByStatus(String status) {
        return ollamaRequestLogRepository.findByStatus(status);
    }

    @Override
    public void deleteById(Long id) {
        ollamaRequestLogRepository.deleteById(id);
    }
    
    @Override
    public List<OllamaRequestLog> findRecentByUserIdAndRequestType(Long userId, String requestType, int limit) {
        return ollamaRequestLogRepository.findTop10ByUserIdAndRequestTypeAndStatusOrderById(userId, requestType, "成功");
    }
} 