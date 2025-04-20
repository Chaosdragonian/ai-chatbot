package com.stone.aichatbot.auth.controller.dto

data class SignUpRequest(
    val email: String,
    val password: String,
    val name: String,
)

data class LoginRequest(
    val email: String,
    val password: String,
)

data class UserResponse(
    val id: Long,
    val email: String,
    val name: String,
    val role: String,
)

data class LoginResponse(
    val token: String,
    val user: UserResponse,
)