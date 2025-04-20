package com.stone.aichatbot.auth.service.dto

data class SignUpCommand(
    val email: String,
    val password: String,
    val name: String,
)

data class LoginCommand(
    val email: String,
    val password: String,
)

data class UserInfo(
    val id: Long,
    val email: String,
    val name: String,
    val role: String,
)

data class LoginResult(
    val token: String,
    val user: UserInfo,
)