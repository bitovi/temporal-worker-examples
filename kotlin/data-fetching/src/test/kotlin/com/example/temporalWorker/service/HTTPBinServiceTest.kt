package com.example.temporalWorker.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.any
import org.mockito.Mockito.anyString
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.HttpHeaders
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Mono

@ExtendWith(MockitoExtension::class)
class HTTPBinServiceTest {

    @Mock
    private lateinit var webClientBuilder: WebClient.Builder

    @Mock
    private lateinit var webClient: WebClient

    @Mock
    private lateinit var requestHeadersUriSpec: WebClient.RequestHeadersUriSpec<*>

    @Mock
    private lateinit var requestHeadersSpec: WebClient.RequestHeadersSpec<*>

    @Mock
    private lateinit var responseSpec: WebClient.ResponseSpec

    @InjectMocks
    private lateinit var httpBinService: HTTPBinService

    @BeforeEach
    fun setUp() {
        Mockito.`when`(webClientBuilder.build()).thenReturn(webClient)
        Mockito.`when`(webClient.get()).thenReturn(requestHeadersUriSpec)
        Mockito.`when`(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec)
        Mockito.`when`(requestHeadersSpec.retrieve()).thenReturn(responseSpec)
        Mockito.`when`(responseSpec.onStatus(any(), any())).thenReturn(responseSpec)
    }

    @Test
    fun `makeRequestWithWebClient should return hex encoded response`() {
        val length = 10
        val responseBody = "responseData"
        val expectedHex = "726573706f6e736544617461"

        Mockito.`when`(responseSpec.bodyToMono(ByteArray::class.java)).thenReturn(Mono.just(responseBody.toByteArray()))

        val result = httpBinService.makeRequestWithWebClient(length)

        assertEquals(expectedHex, result)
    }

    @Test
    fun `makeRequestWithWebClient should handle WebClientResponseException`() {
        val length = 10
        val exception = WebClientResponseException.create(
            404, 
            "Not Found", 
            HttpHeaders.EMPTY, 
            ByteArray(0), 
            null
        )

        Mockito.`when`(responseSpec.bodyToMono(ByteArray::class.java)).thenReturn(Mono.error(exception))

        val thrownException = assertThrows(
            WebClientResponseException::class.java,
            { httpBinService.makeRequestWithWebClient(length) }
        )

        assertEquals(404, thrownException.statusCode.value())
        assertEquals("Not Found", thrownException.statusText)
    }
}
