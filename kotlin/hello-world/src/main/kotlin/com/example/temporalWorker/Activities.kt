package com.example.temporalWorker.activities

import io.temporal.activity.ActivityInterface
import io.temporal.activity.ActivityMethod

@ActivityInterface
interface Activities {
    @ActivityMethod
    fun composeGreeting(greeting: String, name: String): String
}
