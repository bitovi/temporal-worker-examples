package com.example.temporalWorker.activities

class ActivitiesImpl : Activities {
    override fun composeGreeting(greeting: String, name: String): String {
        return "$greeting, $name!"
    }
}
