package com.stone.aichatbot.auth.service

import com.stone.aichatbot.auth.entity.User
import com.stone.aichatbot.auth.entity.UserRole
import com.stone.aichatbot.auth.repository.UserRepository
import com.stone.aichatbot.auth.service.dto.LoginCommand
import com.stone.aichatbot.auth.service.dto.LoginResult
import com.stone.aichatbot.auth.service.dto.SignUpCommand
import com.stone.aichatbot.auth.service.dto.UserInfo
import com.stone.aichatbot.exception.EmailAlreadyExistsException
import com.stone.aichatbot.exception.EmailNotFoundException
import com.stone.aichatbot.exception.PasswordMismatchException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
) {
    @Transactional
    fun signUp(command: SignUpCommand): UserInfo {
        if (userRepository.existsByEmail(command.email)) {
            throw EmailAlreadyExistsException()
        }

        val user = User(
            email = command.email,
            password = passwordEncoder.encode(command.password),
            name = command.name,
            role = UserRole.MEMBER
        )

        val savedUser = userRepository.save(user)
        return UserInfo(
            id = savedUser.id!!,
            email = savedUser.email,
            name = savedUser.name,
            role = savedUser.role.name
        )
    }

    fun login(command: LoginCommand): LoginResult {
        val user = userRepository.findByEmail(command.email)
            .orElseThrow { EmailNotFoundException() }

        if (!passwordEncoder.matches(command.password, user.password)) {
            throw PasswordMismatchException()
        }

        val token = jwtService.generateToken(user)
        val userInfo = UserInfo(
            id = user.id!!,
            email = user.email,
            name = user.name,
            role = user.role.name
        )

        return LoginResult(token, userInfo)
    }
}
