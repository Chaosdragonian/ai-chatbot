package com.stone.aichatbot.chat.dto

import java.time.LocalDateTime

data class ChatRequest(
    val question: String,
    val isStreaming: Boolean = false,
    val model: String? = null,
)

data class ChatResponse(
    val id: Long,
    val question: String,
    val answer: String,
    val createdAt: LocalDateTime,
)

data class ThreadResponse(
    val id: Long,
    val chats: List<ChatResponse>,
    val createdAt: LocalDateTime,
    val lastActivityAt: LocalDateTime,
)

data class ChatListResponse(
    val threads: List<ThreadResponse>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
) 