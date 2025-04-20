package com.stone.aichatbot.chat.controller

import com.stone.aichatbot.chat.dto.ChatListResponse
import com.stone.aichatbot.chat.dto.ChatRequest
import com.stone.aichatbot.chat.dto.ChatResponse
import com.stone.aichatbot.chat.service.ChatService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/chats")
class ChatController(
    private val chatService: ChatService,
) {
    @PostMapping
    suspend fun createChat(
        authentication: Authentication,
        @RequestBody request: ChatRequest,
    ): ResponseEntity<ChatResponse> {
        val userId = authentication.principal as Long
        val response = chatService.createChat(userId, request)
        return ResponseEntity.ok(response)
    }

    @GetMapping
    fun getChats(
        authentication: Authentication,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "desc") sort: String,
    ): ResponseEntity<ChatListResponse> {
        val userId = authentication.principal as Long
        val isAdmin = authentication.authorities.any { it == SimpleGrantedAuthority("ROLE_ADMIN") }
        val response = chatService.getChats(userId, isAdmin, page, size, sort)
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/threads/{threadId}")
    fun deleteThread(
        authentication: Authentication,
        @PathVariable threadId: Long,
    ): ResponseEntity<Unit> {
        val userId = authentication.principal as Long
        val isAdmin = authentication.authorities.any { it == SimpleGrantedAuthority("ROLE_ADMIN") }
        chatService.deleteThread(userId, isAdmin, threadId)
        return ResponseEntity.ok().build()
    }
}
