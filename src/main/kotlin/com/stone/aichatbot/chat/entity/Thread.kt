package com.stone.aichatbot.chat.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "threads")
class Thread(
    @Column(name = "user_id", nullable = false)
    val userId: Long,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    var lastActivityAt: LocalDateTime = LocalDateTime.now(),

    @OneToMany(mappedBy = "thread", cascade = [CascadeType.ALL], orphanRemoval = true)
    val chats: MutableList<Chat> = mutableListOf(),

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
) {
    fun updateLastActivity() {
        lastActivityAt = LocalDateTime.now()
    }
} 