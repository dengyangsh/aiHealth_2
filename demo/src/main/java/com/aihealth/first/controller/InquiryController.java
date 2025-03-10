package com.aihealth.first.controller;

import com.aihealth.first.service.InquiryService;
import com.aihealth.first.service.ModificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inquiry")
public class InquiryController {

    @Autowired
    private InquiryService inquiryService;

    @Autowired
    private ModificationService modificationService;

    @PostMapping("/{userId}/ask")
    public ResponseEntity<String> askQuestion(@PathVariable Long userId, @RequestBody String question) {
        String response = inquiryService.handleInquiry(userId, question);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{userId}/modify")
    public ResponseEntity<String> modifyData(@PathVariable Long userId, @RequestBody String modificationRequest) {
        String response = inquiryService.handleModification(userId, modificationRequest);
        try {
            // 调用service处理修改请求
            modificationService.processModificationResponse(userId, response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing modification: " + e.getMessage());
        }
        return ResponseEntity.ok(response);
    }
} 