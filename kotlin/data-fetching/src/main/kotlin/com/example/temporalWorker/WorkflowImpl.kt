package com.example.temporalWorker.workflows

import io.temporal.activity.ActivityOptions
import io.temporal.workflow.Workflow
import java.time.Duration
import com.example.temporalWorker.activities.Activities
import reactor.core.publisher.Mono

class WorkflowImpl : ExampleWorkflow {
    private val activityOptions = ActivityOptions.newBuilder()
        .setStartToCloseTimeout(Duration.ofSeconds(10))
        .build()

    private val activities: Activities = Workflow.newActivityStub(Activities::class.java, activityOptions)

    override fun createSecretKey(length: kotlin.Int): String {
        val secret = activities.fetchRandomBytes(length)

        return secret
    }
}
