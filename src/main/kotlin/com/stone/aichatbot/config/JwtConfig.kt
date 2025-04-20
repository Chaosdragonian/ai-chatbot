package com.stone.aichatbot.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "jwt")
data class JwtConfig(
    val secret: String,
    val expiration: Long
) 