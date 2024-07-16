package com.example.temporalWorker.activities

import io.temporal.activity.ActivityInterface
import io.temporal.activity.ActivityMethod
import reactor.core.publisher.Mono

@ActivityInterface
interface Activities {
    @ActivityMethod
    fun fetchRandomBytes(length: kotlin.Int): String
}
