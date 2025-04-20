package com.stone.aichatbot.auth.filter

import com.stone.aichatbot.auth.entity.UserRole
import com.stone.aichatbot.auth.service.JwtService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtService: JwtService,
) : OncePerRequestFilter() {
    private val logger = LoggerFactory.getLogger(JwtAuthenticationFilter::class.java)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val authHeader = request.getHeader("Authorization")
        logger.info("Authorization header: $authHeader")

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.info("No valid Authorization header found")
            filterChain.doFilter(request, response)
            return
        }

        val jwt = authHeader.substring(7)
        logger.info("JWT token: $jwt")

        if (!jwtService.validateToken(jwt)) {
            logger.warn("Invalid JWT token")
            filterChain.doFilter(request, response)
            return
        }

        val claims = jwtService.getClaimsFromToken(jwt)
        val userId = when (val id = claims["id"]) {
            is Number -> id.toLong()
            is String -> id.toLong()
            else -> throw IllegalArgumentException("Invalid user ID type")
        }
        val role = claims.get("role", String::class.java) ?: UserRole.MEMBER.name

        logger.info("Authenticated user: id=$userId, role=$role")

        if (SecurityContextHolder.getContext().authentication == null) {
            val authorities = listOf(SimpleGrantedAuthority("ROLE_$role"))

            val authToken = UsernamePasswordAuthenticationToken(
                userId,
                null,
                authorities
            )
            SecurityContextHolder.getContext().authentication = authToken
        }
        filterChain.doFilter(request, response)
    }
}
