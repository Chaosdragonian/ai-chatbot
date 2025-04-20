package com.stone.aichatbot.chat.repository

import com.stone.aichatbot.chat.entity.Thread
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository

interface ThreadRepository : JpaRepository<Thread, Long> {
    @EntityGraph(attributePaths = ["chats"])
    fun findByUserId(userId: Long, pageable: Pageable): Page<Thread>

    @EntityGraph(attributePaths = ["chats"])
    fun findFirstByUserIdOrderByLastActivityAtDesc(userId: Long): Thread?
}
