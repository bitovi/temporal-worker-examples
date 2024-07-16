package com.example.temporalWorker.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContext
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.TrustManagerFactory
import java.io.FileInputStream
import java.security.KeyStore
import java.nio.file.Paths

@Configuration
class SSLConfig() {

    @Bean
    fun sslContext(): SslContext {
        val clientCertPath = Paths.get(System.getenv("TEMPORAL_CERT"))
        val clientKeyPath = Paths.get(System.getenv("TEMPORAL_KEY"))

        return GrpcSslContexts.forClient()
            .keyManager(clientCertPath.toFile(), clientKeyPath.toFile())
            .build() ?: throw RuntimeException("Failed to create mTLS config!")
    }
}
