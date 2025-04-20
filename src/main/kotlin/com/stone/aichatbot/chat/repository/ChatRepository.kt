package com.stone.aichatbot.chat.repository

import com.stone.aichatbot.chat.entity.Chat
import org.springframework.data.jpa.repository.JpaRepository

interface ChatRepository : JpaRepository<Chat, Long> 