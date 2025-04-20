package com.stone.aichatbot.auth.controller

import com.stone.aichatbot.auth.controller.dto.LoginRequest
import com.stone.aichatbot.auth.controller.dto.LoginResponse
import com.stone.aichatbot.auth.controller.dto.SignUpRequest
import com.stone.aichatbot.auth.controller.dto.UserResponse
import com.stone.aichatbot.auth.service.UserService
import com.stone.aichatbot.auth.service.dto.LoginCommand
import com.stone.aichatbot.auth.service.dto.SignUpCommand
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService,
) {
    @PostMapping("/signup")
    fun signUp(@RequestBody request: SignUpRequest): ResponseEntity<UserResponse> {
        val command = SignUpCommand(
            email = request.email,
            password = request.password,
            name = request.name
        )
        val userInfo = userService.signUp(command)
        val response = UserResponse(
            id = userInfo.id,
            email = userInfo.email,
            name = userInfo.name,
            role = userInfo.role
        )
        return ResponseEntity.ok(response)
    }

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<LoginResponse> {
        val command = LoginCommand(
            email = request.email,
            password = request.password
        )
        val result = userService.login(command)
        val response = LoginResponse(
            token = result.token,
            user = UserResponse(
                id = result.user.id,
                email = result.user.email,
                name = result.user.name,
                role = result.user.role
            )
        )
        return ResponseEntity.ok(response)
    }
} 