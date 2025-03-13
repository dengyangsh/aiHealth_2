package com.aihealth.first.controller;

import com.aihealth.first.domain.UserDomain;
import com.aihealth.first.service.InquiryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/inquiry")
public class InquiryController {

    @Autowired
    private InquiryService inquiryService;

    @PostMapping("/{userId}/ask")
    public ResponseEntity<Map<String, Object>> askQuestion(@PathVariable Long userId, @RequestBody UserDomain userDomain) {
        try {
            String response = inquiryService.handleInquiry(userId, userDomain.getQuestion());
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("answer", response);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            
            return ResponseEntity.ok(error);
        }
    }
} 