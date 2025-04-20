package com.stone.aichatbot.report.service

import com.stone.aichatbot.auth.repository.UserRepository
import com.stone.aichatbot.chat.repository.ChatRepository
import com.stone.aichatbot.report.dto.UserActivityResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.File
import java.io.FileWriter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Service
class ReportService(
    private val userRepository: UserRepository,
    private val chatRepository: ChatRepository,
) {
    private val reportDir = "reports"

    init {
        File(reportDir).mkdirs()
    }

    @Transactional(readOnly = true)
    fun getUserActivity(): UserActivityResponse {
        val endTime = LocalDateTime.now()
        val startTime = endTime.minus(1, ChronoUnit.DAYS)

        val signUpCount = userRepository.countByCreatedAtBetween(startTime, endTime)
        val loginCount = userRepository.countByLastLoginAtBetween(startTime, endTime)
        val chatCreationCount = chatRepository.countByCreatedAtBetween(startTime, endTime)

        return UserActivityResponse(
            signUpCount = signUpCount.toInt(),
            loginCount = loginCount.toInt(),
            chatCreationCount = chatCreationCount.toInt(),
            startTime = startTime,
            endTime = endTime
        )
    }

    @Transactional(readOnly = true)
    fun generateReport(): String {
        val endTime = LocalDateTime.now()
        val startTime = endTime.minus(1, ChronoUnit.DAYS)

        val chats = chatRepository.findByCreatedAtBetween(startTime, endTime)

        val fileName = "chat-report-${LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))}.csv"
        val file = File(reportDir, fileName)

        FileWriter(file).use { writer ->
            // CSV 헤더 작성
            writer.write("Chat ID,User ID,Question,Answer,Created At\n")

            // 데이터 작성
            chats.forEach { chat ->
                writer.write(
                    "${chat.id},${chat.userId},\"${chat.question}\",\"${chat.answer}\",${chat.createdAt}\n"
                )
            }
        }

        return "/api/reports/download/$fileName"
    }
}
