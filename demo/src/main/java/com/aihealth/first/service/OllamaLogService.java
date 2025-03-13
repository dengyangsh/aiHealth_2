package com.aihealth.first.service;

import com.aihealth.first.entity.OllamaRequestLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OllamaLogService {

    /**
     * 保存Ollama请求日志
     * 
     * @param ollamaRequestLog 请求日志实体
     * @return 保存后的请求日志实体
     */
    OllamaRequestLog saveRequestLog(OllamaRequestLog ollamaRequestLog);

    /**
     * 创建并保存Ollama请求日志
     * 
     * @param userId 用户ID
     * @param modelName 模型名称
     * @param originalQuestion 原始问题
     * @param rawResponse 原始响应
     * @param processedResponse 处理后的响应
     * @param processingTimeMs 处理时间（毫秒）
     * @param status 状态
     * @param questionAnswer 问题答案
     * @return 保存后的请求日志实体
     */
    OllamaRequestLog createRequestLog(Long userId, String modelName, String originalQuestion,
                                     String rawResponse, String processedResponse,
                                     long processingTimeMs, String status, String questionAnswer);

    /**
     * 创建并保存错误日志
     * 
     * @param userId 用户ID
     * @param modelName 模型名称
     * @param requestPrompt 请求的prompt
     * @param errorMessage 错误信息
     * @return 保存后的请求日志实体
     */
    OllamaRequestLog createErrorLog(Long userId, String modelName, String requestPrompt, String errorMessage);

    /**
     * 根据ID查询请求日志
     * 
     * @param id 日志ID
     * @return 请求日志实体
     */
    Optional<OllamaRequestLog> findById(Long id);

    /**
     * 查询所有请求日志
     * 
     * @return 请求日志列表
     */
    List<OllamaRequestLog> findAll();

    /**
     * 根据用户ID查询请求日志
     * 
     * @param userId 用户ID
     * @return 请求日志列表
     */
    List<OllamaRequestLog> findByUserId(Long userId);

    /**
     * 根据用户ID分页查询请求日志
     * 
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 分页请求日志
     */
    Page<OllamaRequestLog> findByUserId(Long userId, Pageable pageable);

    /**
     * 根据模型名称查询请求日志
     * 
     * @param modelName 模型名称
     * @return 请求日志列表
     */
    List<OllamaRequestLog> findByModelName(String modelName);

    /**
     * 根据时间范围查询请求日志
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 请求日志列表
     */
    List<OllamaRequestLog> findByRequestTimeBetween(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 根据用户ID和时间范围查询请求日志
     * 
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 请求日志列表
     */
    List<OllamaRequestLog> findByUserIdAndRequestTimeBetween(Long userId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 根据状态查询请求日志
     * 
     * @param status 状态
     * @return 请求日志列表
     */
    List<OllamaRequestLog> findByStatus(String status);

    /**
     * 删除请求日志
     * 
     * @param id 日志ID
     */
    void deleteById(Long id);
    
    /**
     * 根据用户ID和请求类型查询最近的记录，并按ID从小到大排序
     * 
     * @param userId 用户ID
     * @param requestType 请求类型
     * @param limit 限制返回记录数
     * @return 请求日志列表
     */
    List<OllamaRequestLog> findRecentByUserIdAndRequestType(Long userId, String requestType, int limit);
} 