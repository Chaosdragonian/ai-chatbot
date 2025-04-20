package com.stone.aichatbot.chat.repository

import com.stone.aichatbot.chat.entity.Chat
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface ChatRepository : JpaRepository<Chat, Long> {
    fun findByThreadId(threadId: Long): List<Chat>
    fun findByUserId(userId: Long): List<Chat>
    fun countByCreatedAtBetween(startTime: LocalDateTime, endTime: LocalDateTime): Long
    fun findByCreatedAtBetween(startTime: LocalDateTime, endTime: LocalDateTime): List<Chat>
}