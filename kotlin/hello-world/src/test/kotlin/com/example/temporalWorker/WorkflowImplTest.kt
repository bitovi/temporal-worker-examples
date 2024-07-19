package com.example.temporalWorker

import com.example.temporalWorker.activities.ActivitiesImpl
import com.example.temporalWorker.activities.Activities
import io.temporal.testing.TestWorkflowEnvironment
import io.temporal.client.WorkflowClient
import io.temporal.client.WorkflowOptions
import io.temporal.worker.Worker
import io.temporal.worker.WorkerFactory
import io.temporal.client.WorkflowStub
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class WorkflowImplTest {
    private lateinit var testEnvironment: TestWorkflowEnvironment
    private lateinit var workflowClient: WorkflowClient
    private lateinit var workerFactory: WorkerFactory
    private lateinit var worker: Worker
    private lateinit var workflowStub: ExampleWorkflow
    private lateinit var myActivities: Activities

    @BeforeEach
    fun setUp() {
        testEnvironment = TestWorkflowEnvironment.newInstance()
        workflowClient = testEnvironment.workflowClient
        worker = testEnvironment.newWorker("test-task-queue")

        myActivities = Mockito.mock(ActivitiesImpl::class.java)
        worker.registerActivitiesImplementations(myActivities)
        worker.registerWorkflowImplementationTypes(WorkflowImpl::class.java)

        testEnvironment.start()
    }

    @AfterEach
    fun tearDown() {
        testEnvironment.close()
    }

    @Test
    fun testGreet() {
        `when`(myActivities.composeGreeting("Hello", "World")).thenReturn("Hello, World!")

        val workflowOptions = WorkflowOptions.newBuilder()
            .setTaskQueue("test-task-queue")
            .build()

        workflowStub = workflowClient.newWorkflowStub(ExampleWorkflow::class.java, workflowOptions)

        val resultFuture = WorkflowClient.execute(workflowStub::greet, "World")
        val result = resultFuture.get()

        assertEquals("Hello, World!", result)
    }
}
