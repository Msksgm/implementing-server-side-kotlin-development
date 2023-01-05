package com.example.implementingserversidekotlindevelopment.presentation

import com.example.implementingserversidekotlindevelopment.openapi.generated.controller.HealthCheckApi
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

/**
 * ヘルスチェックエンドポイントのコントローラー
 *
 */
@RestController
class HealthCheckController : HealthCheckApi {
    override fun healthCheck(): ResponseEntity<String> {
        return ResponseEntity("OK", HttpStatus.OK)
    }
}
