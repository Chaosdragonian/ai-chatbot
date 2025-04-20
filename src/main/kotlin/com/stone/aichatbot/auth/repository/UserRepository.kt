package com.stone.aichatbot.auth.repository

import com.stone.aichatbot.auth.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.Optional

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun findByName(name: String): Optional<User>
    fun findByEmail(email: String): Optional<User>
    fun existsByEmail(email: String): Boolean

    fun countByCreatedAtBetween(startTime: LocalDateTime, endTime: LocalDateTime): Long
    fun countByLastLoginAtBetween(startTime: LocalDateTime, endTime: LocalDateTime): Long
}
