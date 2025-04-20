package com.stone.aichatbot.feedback.controller

import com.stone.aichatbot.feedback.dto.FeedbackListResponse
import com.stone.aichatbot.feedback.dto.FeedbackRequest
import com.stone.aichatbot.feedback.dto.FeedbackResponse
import com.stone.aichatbot.feedback.dto.FeedbackStatusUpdateRequest
import com.stone.aichatbot.feedback.service.FeedbackService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/feedbacks")
class FeedbackController(
    private val feedbackService: FeedbackService,
) {
    @PostMapping
    fun createFeedback(
        authentication: Authentication,
        @RequestBody request: FeedbackRequest,
    ): ResponseEntity<FeedbackResponse> {
        val userId = authentication.principal as Long
        val isAdmin = authentication.authorities.any { it == SimpleGrantedAuthority("ROLE_ADMIN") }
        val response = feedbackService.createFeedback(userId, isAdmin, request)
        return ResponseEntity.ok(response)
    }

    @GetMapping
    fun getFeedbacks(
        authentication: Authentication,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "desc") sort: String,
        @RequestParam(required = false) isPositive: Boolean?,
    ): ResponseEntity<FeedbackListResponse> {
        val userId = authentication.principal as Long
        val isAdmin = authentication.authorities.any { it == SimpleGrantedAuthority("ROLE_ADMIN") }
        val response = feedbackService.getFeedbacks(userId, isAdmin, page, size, sort, isPositive)
        return ResponseEntity.ok(response)
    }

    @PatchMapping("/{feedbackId}/status")
    fun updateFeedbackStatus(
        authentication: Authentication,
        @PathVariable feedbackId: Long,
        @RequestBody request: FeedbackStatusUpdateRequest,
    ): ResponseEntity<FeedbackResponse> {
        val userId = authentication.principal as Long
        val isAdmin = authentication.authorities.any { it == SimpleGrantedAuthority("ROLE_ADMIN") }
        val response = feedbackService.updateFeedbackStatus(userId, isAdmin, feedbackId, request)
        return ResponseEntity.ok(response)
    }
} 