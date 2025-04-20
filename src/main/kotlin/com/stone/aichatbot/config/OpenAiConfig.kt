package com.stone.aichatbot.config

import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIConfig
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "openai")
class OpenAiConfig {
    private val logger = LoggerFactory.getLogger(OpenAiConfig::class.java)

    lateinit var apiKey: String
    var model: String = "gpt-4.1"
    var maxTokens: Int = 1000
    var temperature: Double = 0.7

    @Bean
    fun openAI(): OpenAI {
        logger.info("Initializing OpenAI client with model: $model")
        logger.info("OpenAI API Key: $apiKey")
        val config = OpenAIConfig(
            token = apiKey
        )
        return OpenAI(config)
    }

    @Bean
    fun chatCompletionRequest(): ChatCompletionRequest {
        return ChatCompletionRequest(
            model = ModelId(model),
            messages = listOf(
                ChatMessage(
                    role = ChatRole.System,
                    content = "You are a helpful AI assistant."
                )
            ),
            maxTokens = maxTokens,
            temperature = temperature
        )
    }
}
