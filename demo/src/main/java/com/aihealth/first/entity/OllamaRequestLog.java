package com.aihealth.first.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "ollama_request_logs")
public class OllamaRequestLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 日志ID

    @Column(name = "user_id")
    private Long userId; // 用户ID，可以为空（如系统请求）

    @Column(name = "model_name", nullable = false)
    private String modelName; // 模型名称

    @Column(name = "request_prompt", nullable = false, columnDefinition = "TEXT")
    private String requestPrompt; // 请求的prompt内容

    @Column(name = "raw_response", columnDefinition = "TEXT")
    private String rawResponse; // 原始响应内容

    @Column(name = "processed_response", columnDefinition = "TEXT")
    private String processedResponse; // 处理后的响应内容

    @Column(name = "request_time", nullable = false)
    private LocalDateTime requestTime; // 请求时间

    @Column(name = "response_time")
    private LocalDateTime responseTime; // 响应时间

    @Column(name = "processing_time_ms")
    private Long processingTimeMs; // 处理时间（毫秒）

    @Column(name = "status")
    private String status; // 请求状态：成功/失败

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage; // 错误信息（如果有）

    @Column(name = "created_at")
    private LocalDateTime createdAt; // 创建时间

    @Column(name = "question_answer", columnDefinition = "TEXT")
    private String questionAnswer; // 解析后的question.answer

    @Column(name = "original_question", columnDefinition = "TEXT")
    private String originalQuestion; // 原始的用户问题

    @Column(name = "request_type")
    private String requestType; // 请求类型：DeepSeek或Ollama

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
} 