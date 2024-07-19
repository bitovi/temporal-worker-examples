package com.example.temporalWorker

import com.example.temporalWorker.activities.Activities
import io.temporal.activity.ActivityOptions
import io.temporal.workflow.Workflow
import java.time.Duration

class WorkflowImpl : ExampleWorkflow {
    private val activityOptions = ActivityOptions.newBuilder()
        .setStartToCloseTimeout(Duration.ofMinutes(10))
        .build()

    private val activities: Activities = Workflow.newActivityStub(Activities::class.java, activityOptions)

    override fun greet(name: String): String {
        return activities.composeGreeting("Hello", name)
    }
}
