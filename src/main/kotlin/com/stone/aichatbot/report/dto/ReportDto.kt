package com.stone.aichatbot.report.dto

import java.time.LocalDateTime

data class UserActivityResponse(
    val signUpCount: Int,
    val loginCount: Int,
    val chatCreationCount: Int,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
)

data class ReportRequest(
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
)

data class ChatReport(
    val chatId: Long,
    val userId: Long,
    val question: String,
    val answer: String,
    val createdAt: LocalDateTime,
)