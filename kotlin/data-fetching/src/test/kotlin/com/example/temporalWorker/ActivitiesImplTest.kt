package com.example.temporalWorker.activities

import com.example.temporalWorker.service.HTTPBinService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class ActivitiesImplTest {

    @Mock
    private lateinit var httpBinService: HTTPBinService

    @InjectMocks
    private lateinit var myActivities: ActivitiesImpl

    @Test
    fun `fetchRandomBytes should return hex encoded response`() {
        val length = 10
        val responseHex = "726573706f6e736544617461"

        Mockito.`when`(httpBinService.makeRequestWithWebClient(length)).thenReturn(responseHex)

        val result = myActivities.fetchRandomBytes(length)

        assertEquals(responseHex, result)
    }

    @Test
    fun `fetchRandomBytes should handle exceptions`() {
        val length = 10
        val errorMessage = "Error: 404 - Not Found"

        Mockito.`when`(httpBinService.makeRequestWithWebClient(length)).thenReturn(errorMessage)

        val result = myActivities.fetchRandomBytes(length)

        assertEquals(errorMessage, result)
    }
}
