package com.stone.aichatbot.report.controller

import com.stone.aichatbot.report.dto.UserActivityResponse
import com.stone.aichatbot.report.service.ReportService
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.web.bind.annotation.*
import java.io.File

@RestController
@RequestMapping("/api/reports")
class ReportController(
    private val reportService: ReportService,
) {
    @GetMapping("/user-activity")
    fun getUserActivity(authentication: Authentication): ResponseEntity<UserActivityResponse> {
        val isAdmin = authentication.authorities.any { it == SimpleGrantedAuthority("ROLE_ADMIN") }
        if (!isAdmin) {
            throw IllegalAccessException("You do not have permission to access this resource.")
        }
        val response = reportService.getUserActivity()
        return ResponseEntity.ok(response)
    }

    @PostMapping("/generate")
    fun generateReport(authentication: Authentication): ResponseEntity<String> {
        val isAdmin = authentication.authorities.any { it == SimpleGrantedAuthority("ROLE_ADMIN") }
        if (!isAdmin) {
            throw IllegalAccessException("You do not have permission to access this resource.")
        }
        val downloadUrl = reportService.generateReport()
        return ResponseEntity.ok(downloadUrl)
    }

    @GetMapping("/download/{fileName}")
    fun downloadReport(@PathVariable fileName: String): ResponseEntity<ByteArray> {
        val file = File("reports", fileName)
        if (!file.exists()) {
            return ResponseEntity.notFound().build()
        }

        val headers = HttpHeaders().apply {
            contentType = MediaType.parseMediaType("text/csv; charset=UTF-8")
            setContentDispositionFormData("attachment", fileName)
            setContentLength(file.length())
        }

        return ResponseEntity.ok()
            .headers(headers)
            .body(file.readBytes())
    }
}
