package com.aihealth.first.service;

public interface InquiryService {

    /**
     * 处理用户的健身咨询请求
     * @param userId 用户ID
     * @param question 用户问题
     * @return 回答内容
     */
    String handleInquiry(Long userId, String question);
   
}