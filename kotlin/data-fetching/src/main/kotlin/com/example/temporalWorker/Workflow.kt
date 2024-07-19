package com.example.temporalWorker.workflows

import io.temporal.workflow.WorkflowInterface
import io.temporal.workflow.WorkflowMethod
import reactor.core.publisher.Mono

@WorkflowInterface
interface ExampleWorkflow {
    @WorkflowMethod
    fun createSecretKey(length: kotlin.Int): String
}
