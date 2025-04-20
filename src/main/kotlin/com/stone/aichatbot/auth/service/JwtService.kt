package com.stone.aichatbot.auth.service

import com.stone.aichatbot.auth.entity.User
import com.stone.aichatbot.config.JwtConfig
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Service
import java.util.*

@Service
class JwtService(
    private val jwtConfig: JwtConfig,
) {
    private val key = Keys.hmacShaKeyFor(jwtConfig.secret.toByteArray())

    fun generateToken(user: User): String {
        val now = Date()
        val expiration = Date(now.time + jwtConfig.expiration)

        return Jwts.builder()
            .setSubject(user.email)
            .claim("id", user.id)
            .claim("role", user.role.name)
            .setIssuedAt(now)
            .setExpiration(expiration)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()
    }

    fun validateToken(token: String): Boolean {
        return try {
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getEmailFromToken(token: String): String {
        val claims = Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body

        return claims.subject
    }
} 