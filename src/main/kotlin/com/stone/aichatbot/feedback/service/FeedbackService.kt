package com.stone.aichatbot.feedback.service

import com.stone.aichatbot.chat.repository.ChatRepository
import com.stone.aichatbot.feedback.dto.FeedbackListResponse
import com.stone.aichatbot.feedback.dto.FeedbackRequest
import com.stone.aichatbot.feedback.dto.FeedbackResponse
import com.stone.aichatbot.feedback.dto.FeedbackStatusUpdateRequest
import com.stone.aichatbot.feedback.entity.Feedback
import com.stone.aichatbot.feedback.entity.FeedbackStatus
import com.stone.aichatbot.feedback.repository.FeedbackRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FeedbackService(
    private val feedbackRepository: FeedbackRepository,
    private val chatRepository: ChatRepository,
) {
    @Transactional
    fun createFeedback(userId: Long, isAdmin: Boolean, request: FeedbackRequest): FeedbackResponse {
        val chat = chatRepository.findById(request.chatId)
            .orElseThrow { IllegalArgumentException("Chat not found") }

        if (!isAdmin && chat.userId != userId) {
            throw IllegalArgumentException("You can only create feedback for your own chats")
        }

        if (feedbackRepository.existsByChatIdAndUserId(request.chatId, userId)) {
            throw IllegalArgumentException("You have already created feedback for this chat")
        }

        val feedback = Feedback(
            userId = userId,
            chatId = request.chatId,
            isPositive = request.isPositive,
        )

        val savedFeedback = feedbackRepository.save(feedback)
        return FeedbackResponse(
            id = savedFeedback.id!!,
            userId = savedFeedback.userId,
            chatId = savedFeedback.chatId,
            isPositive = savedFeedback.isPositive,
            createdAt = savedFeedback.createdAt,
            status = savedFeedback.status.name,
        )
    }

    @Transactional(readOnly = true)
    fun getFeedbacks(
        userId: Long,
        isAdmin: Boolean,
        page: Int,
        size: Int,
        sort: String,
        isPositive: Boolean?,
    ): FeedbackListResponse {
        val pageable = PageRequest.of(
            page,
            size,
            Sort.by(if (sort == "desc") Sort.Direction.DESC else Sort.Direction.ASC, "createdAt")
        )

        val feedbacks = if (isAdmin) {
            if (isPositive != null) {
                feedbackRepository.findAllByIsPositive(isPositive, pageable)
            } else {
                feedbackRepository.findAll(pageable)
            }
        } else {
            if (isPositive != null) {
                feedbackRepository.findByUserIdAndIsPositive(userId, isPositive, pageable)
            } else {
                feedbackRepository.findByUserId(userId, pageable)
            }
        }

        val feedbackResponses = feedbacks.content.map { feedback ->
            FeedbackResponse(
                id = feedback.id!!,
                userId = feedback.userId,
                chatId = feedback.chatId,
                isPositive = feedback.isPositive,
                createdAt = feedback.createdAt,
                status = feedback.status.name,
            )
        }

        return FeedbackListResponse(
            feedbacks = feedbackResponses,
            page = feedbacks.number,
            size = feedbacks.size,
            totalElements = feedbacks.totalElements,
            totalPages = feedbacks.totalPages,
        )
    }

    @Transactional
    fun updateFeedbackStatus(
        userId: Long,
        isAdmin: Boolean,
        feedbackId: Long,
        request: FeedbackStatusUpdateRequest,
    ): FeedbackResponse {
        if (!isAdmin) {
            throw IllegalArgumentException("Only admin can update feedback status")
        }

        val feedback = feedbackRepository.findById(feedbackId)
            .orElseThrow { IllegalArgumentException("Feedback not found") }

        val updatedFeedback = feedback.copy(
            status = FeedbackStatus.valueOf(request.status.uppercase())
        )

        val savedFeedback = feedbackRepository.save(updatedFeedback)
        return FeedbackResponse(
            id = savedFeedback.id!!,
            userId = savedFeedback.userId,
            chatId = savedFeedback.chatId,
            isPositive = savedFeedback.isPositive,
            createdAt = savedFeedback.createdAt,
            status = savedFeedback.status.name,
        )
    }
} 