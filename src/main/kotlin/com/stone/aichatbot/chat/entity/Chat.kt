package com.stone.aichatbot.chat.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "chats")
data class Chat(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, length = 4000)
    val question: String,

    @Column(nullable = false, columnDefinition = "TEXT")
    val answer: String,

    @Column(name = "user_id", nullable = false)
    val userId: Long,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "thread_id", nullable = false)
    val thread: Thread,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
)
