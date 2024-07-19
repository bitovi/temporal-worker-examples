package com.example.temporalWorker.activities

import com.example.temporalWorker.activities.ActivitiesImpl
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ActivitiesImplTest {

    @Test
    fun testComposeGreeting() {
        val activities = ActivitiesImpl()
        val greeting = "Hello"
        val name = "World"

        val result = activities.composeGreeting(greeting, name)

        assertEquals("Hello, World!", result)
    }
}
