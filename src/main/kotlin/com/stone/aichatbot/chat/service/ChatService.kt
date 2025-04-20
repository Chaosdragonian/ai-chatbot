package com.stone.aichatbot.chat.service

import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.stone.aichatbot.chat.dto.ChatListResponse
import com.stone.aichatbot.chat.dto.ChatRequest
import com.stone.aichatbot.chat.dto.ChatResponse
import com.stone.aichatbot.chat.dto.ThreadResponse
import com.stone.aichatbot.chat.entity.Chat
import com.stone.aichatbot.chat.entity.Thread
import com.stone.aichatbot.chat.repository.ChatRepository
import com.stone.aichatbot.chat.repository.ThreadRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Service
class ChatService(
    private val chatRepository: ChatRepository,
    private val threadRepository: ThreadRepository,
    private val openAI: OpenAI,
    private val chatCompletionRequest: ChatCompletionRequest,
) {
    @Transactional
    suspend fun createChat(userId: Long, request: ChatRequest): ChatResponse {
        val thread = getOrCreateThread(userId)
        val answer = generateAnswer(request.question, request.model, thread)

        val chat = Chat(
            question = request.question,
            answer = answer,
            userId = userId,
            thread = thread
        )

        // Thread를 먼저 저장
        val savedThread = withContext(Dispatchers.IO) {
            threadRepository.save(thread)
        }

        // Chat을 저장
        val savedChat = withContext(Dispatchers.IO) {
            chatRepository.save(chat)
        }

        return ChatResponse(
            id = savedChat.id!!,
            question = savedChat.question,
            answer = savedChat.answer.toString(),
            createdAt = savedChat.createdAt
        )
    }

    @Transactional(readOnly = true)
    fun getChats(
        userId: Long,
        isAdmin: Boolean,
        page: Int,
        size: Int,
        sort: String,
    ): ChatListResponse {
        val pageable = PageRequest.of(
            page,
            size,
            Sort.by(if (sort == "desc") Sort.Direction.DESC else Sort.Direction.ASC, "createdAt")
        )

        val threads: Page<Thread>
        if (isAdmin) {
            threads = threadRepository.findAll(pageable)
        } else {
            threads = threadRepository.findByUserId(userId, pageable)
        }

        val threadResponses = threads.content.map { thread ->
            ThreadResponse(
                id = thread.id!!,
                chats = thread.chats.map { chat ->
                    ChatResponse(
                        id = chat.id!!,
                        question = chat.question,
                        answer = chat.answer.toString(),
                        createdAt = chat.createdAt
                    )
                },
                createdAt = thread.createdAt,
                lastActivityAt = thread.lastActivityAt
            )
        }

        return ChatListResponse(
            threads = threadResponses,
            page = threads.number,
            size = threads.size,
            totalElements = threads.totalElements,
            totalPages = threads.totalPages
        )
    }

    @Transactional
    fun deleteThread(userId: Long, isAdmin: Boolean, threadId: Long) {
        val thread = threadRepository.findById(threadId)
            .orElseThrow { IllegalArgumentException("Thread not found") }

        if (!isAdmin && thread.userId != userId) {
            throw IllegalArgumentException("You can only delete your own threads")
        }

        threadRepository.delete(thread)
    }

    private fun getOrCreateThread(userId: Long): Thread {
        val lastThread = threadRepository.findFirstByUserIdOrderByLastActivityAtDesc(userId)
        return if (lastThread != null && isWithinTimeLimit(lastThread.lastActivityAt)) {
            lastThread
        } else {
            Thread(userId = userId)
        }
    }

    private fun isWithinTimeLimit(lastActivity: LocalDateTime): Boolean {
        val now = LocalDateTime.now()
        return ChronoUnit.MINUTES.between(lastActivity, now) < 30
    }

    @Transactional
    public suspend fun generateAnswer(question: String, model: String?, thread: Thread): String {
        // chats 컬렉션을 명시적으로 로드
        println("threadsize : ${thread.chats.size}")

        val messages = mutableListOf(
            ChatMessage(
                role = ChatRole.System,
                content = "You are a helpful AI assistant."
            )
        )

        // 이전 대화 내용을 컨텍스트로 추가
        thread.chats.forEach { chat ->
            messages.add(
                ChatMessage(
                    role = ChatRole.User,
                    content = chat.question
                )
            )
            messages.add(
                ChatMessage(
                    role = ChatRole.Assistant,
                    content = chat.answer
                )
            )
        }

        // 현재 질문 추가
        messages.add(
            ChatMessage(
                role = ChatRole.User,
                content = question
            )
        )

        val request = ChatCompletionRequest(
            model = ModelId(model ?: "gpt-4.1"),
            messages = messages,
            maxTokens = chatCompletionRequest.maxTokens,
            temperature = chatCompletionRequest.temperature
        )

        val completion = openAI.chatCompletion(request)
        return completion.choices.first().message.content ?: "죄송합니다. 응답을 생성할 수 없습니다."
    }
}
