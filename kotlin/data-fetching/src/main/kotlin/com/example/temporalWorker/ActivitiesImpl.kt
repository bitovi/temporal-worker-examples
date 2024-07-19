package com.example.temporalWorker.activities

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import com.example.temporalWorker.service.HTTPBinService
import reactor.core.publisher.Mono

@Component
class ActivitiesImpl @Autowired constructor(
    private val service: HTTPBinService
) : Activities {
    override fun fetchRandomBytes(length: kotlin.Int): String {
        return service.makeRequestWithWebClient(length)
    }
}