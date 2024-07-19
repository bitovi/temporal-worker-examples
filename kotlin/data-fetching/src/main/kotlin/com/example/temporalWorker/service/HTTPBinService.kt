package com.example.temporalWorker.service

import org.springframework.http.HttpStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Mono
import java.util.Base64

@Service
class HTTPBinService(
    @Autowired private val webClientBuilder: WebClient.Builder
) {
    fun makeRequestWithWebClient(length: kotlin.Int): String {
        val webClient = webClientBuilder.build()
        val url = "https://httpbin.org/bytes/$length"
        val response = webClient.get()
            .uri(url)
            .retrieve()
            .onStatus({ status -> status != HttpStatus.OK }) { response ->
                response.bodyToMono(String::class.java).flatMap { body ->
                    Mono.error(
                        WebClientResponseException.create(
                            response.statusCode().value(),
                            response.statusCode().toString(),
                            response.headers().asHttpHeaders(),
                            body.toByteArray(),
                            null
                        )
                    )
                }
            }
            .bodyToMono(ByteArray::class.java)
            .map { bytes -> bytesToHex(bytes) }
            .block() ?: throw RuntimeException("Failed to fetch data")

        return response
    }

    private fun bytesToHex(bytes: ByteArray): String {
        val hexString = StringBuilder()
        for (byte in bytes) {
            val hex = String.format("%02x", byte)
            hexString.append(hex)
        }
        return hexString.toString()
    }
}
