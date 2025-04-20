package com.stone.aichatbot.feedback.dto

import java.time.LocalDateTime

data class FeedbackRequest(
    val chatId: Long,
    val isPositive: Boolean,
)

data class FeedbackResponse(
    val id: Long,
    val userId: Long,
    val chatId: Long,
    val isPositive: Boolean,
    val createdAt: LocalDateTime,
    val status: String,
)

data class FeedbackListResponse(
    val feedbacks: List<FeedbackResponse>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
)

data class FeedbackStatusUpdateRequest(
    val status: String,
) 