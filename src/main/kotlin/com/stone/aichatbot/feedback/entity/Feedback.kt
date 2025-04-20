package com.stone.aichatbot.feedback.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "feedbacks")
data class Feedback(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "user_id", nullable = false)
    val userId: Long,

    @Column(name = "chat_id", nullable = false)
    val chatId: Long,

    @Column(nullable = false)
    val isPositive: Boolean,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val status: FeedbackStatus = FeedbackStatus.PENDING,
)

enum class FeedbackStatus {
    PENDING,
    RESOLVED,
}