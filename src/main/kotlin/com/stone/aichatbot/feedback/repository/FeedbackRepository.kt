package com.stone.aichatbot.feedback.repository

import com.stone.aichatbot.feedback.entity.Feedback
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface FeedbackRepository : JpaRepository<Feedback, Long> {
    fun findByUserId(userId: Long, pageable: Pageable): Page<Feedback>
    fun findByUserIdAndIsPositive(userId: Long, isPositive: Boolean, pageable: Pageable): Page<Feedback>
    fun findByChatIdAndUserId(chatId: Long, userId: Long): Feedback?
    fun existsByChatIdAndUserId(chatId: Long, userId: Long): Boolean
    fun findAllByIsPositive(isPositive: Boolean, pageable: Pageable): Page<Feedback>
}