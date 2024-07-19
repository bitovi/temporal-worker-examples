package com.example.temporalWorker

import io.temporal.workflow.WorkflowInterface
import io.temporal.workflow.WorkflowMethod

@WorkflowInterface
interface ExampleWorkflow {
    @WorkflowMethod
    fun greet(name: String): String
}
