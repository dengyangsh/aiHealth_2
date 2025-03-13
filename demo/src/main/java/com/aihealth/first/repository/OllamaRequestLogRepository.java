package com.aihealth.first.repository;

import com.aihealth.first.entity.OllamaRequestLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OllamaRequestLogRepository extends JpaRepository<OllamaRequestLog, Long> {
    
    /**
     * 根据用户ID查询请求日志
     */
    List<OllamaRequestLog> findByUserId(Long userId);
    
    /**
     * 根据用户ID分页查询请求日志
     */
    Page<OllamaRequestLog> findByUserId(Long userId, Pageable pageable);
    
    /**
     * 根据模型名称查询请求日志
     */
    List<OllamaRequestLog> findByModelName(String modelName);
    
    /**
     * 根据请求时间范围查询请求日志
     */
    List<OllamaRequestLog> findByRequestTimeBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 根据用户ID和请求时间范围查询请求日志
     */
    List<OllamaRequestLog> findByUserIdAndRequestTimeBetween(Long userId, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 根据状态查询请求日志
     */
    List<OllamaRequestLog> findByStatus(String status);
    
    /**
     * 根据用户ID和请求类型查询最近的记录，并按ID排序
     * 
     * @param userId 用户ID
     * @param requestType 请求类型
     * @param pageable 分页参数
     * @return 请求日志列表
     */
    @Query("SELECT o FROM OllamaRequestLog o WHERE o.userId = :userId AND o.requestType = :requestType AND o.status = '成功' ORDER BY o.id DESC")
    Page<OllamaRequestLog> findRecentByUserIdAndRequestType(@Param("userId") Long userId, @Param("requestType") String requestType, Pageable pageable);
    
    /**
     * 根据用户ID和请求类型查询最近的记录，并按ID从小到大排序
     * 
     * @param userId 用户ID
     * @param requestType 请求类型
     * @param limit 限制返回记录数
     * @return 请求日志列表
     */
    List<OllamaRequestLog> findTop10ByUserIdAndRequestTypeAndStatusOrderById(Long userId, String requestType, String status);
} 