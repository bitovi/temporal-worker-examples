package com.example.temporalWorker.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContext
import java.nio.file.Paths

@Configuration
class SSLConfig() {

    @Bean
    fun sslContext(): SslContext? {
        val clientCertPath = System.getenv("TEMPORAL_CERT")?.let { Paths.get(it) }
        val clientKeyPath = System.getenv("TEMPORAL_KEY")?.let { Paths.get(it) }

        if (clientCertPath == null || clientKeyPath == null) {
            return null
        }

        return GrpcSslContexts.forClient()
            .keyManager(clientCertPath.toFile(), clientKeyPath.toFile())
            .build() ?: throw RuntimeException("Failed to create mTLS config!")
    }
}
