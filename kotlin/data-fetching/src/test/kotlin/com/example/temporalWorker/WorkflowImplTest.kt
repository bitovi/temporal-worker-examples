package com.example.temporalWorker.workflows

import org.springframework.web.reactive.function.client.WebClient
import com.example.temporalWorker.activities.ActivitiesImpl
import com.example.temporalWorker.activities.Activities
import com.example.temporalWorker.service.HTTPBinService
import io.temporal.client.WorkflowClient
import io.temporal.client.WorkflowOptions
import io.temporal.testing.TestWorkflowEnvironment
import io.temporal.worker.Worker
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock

class WorkflowImplTest {

    private lateinit var testEnvironment: TestWorkflowEnvironment
    private lateinit var worker: Worker
    private lateinit var workflowClient: WorkflowClient
    private lateinit var workflowStub: ExampleWorkflow
    private lateinit var myActivities: Activities

    @BeforeEach
    fun setUp() {
        testEnvironment = TestWorkflowEnvironment.newInstance()
        workflowClient = testEnvironment.workflowClient
        worker = testEnvironment.newWorker("test-task-queue")
        
        myActivities = mock(ActivitiesImpl::class.java)
        worker.registerActivitiesImplementations(myActivities)
        worker.registerWorkflowImplementationTypes(WorkflowImpl::class.java)
        
        testEnvironment.start()
    }

    @AfterEach
    fun tearDown() {
        testEnvironment.close()
    }

    @Test
    fun `createSecretKey should return hex encoded response`() {
        val length = 10
        val responseHex = "726573706f6e736544617461"
        
        Mockito.`when`(myActivities.fetchRandomBytes(length)).thenReturn("responseData".toByteArray().toHexString())

        val workflowOptions = WorkflowOptions.newBuilder()
            .setTaskQueue("test-task-queue")
            .build()

        workflowStub = workflowClient.newWorkflowStub(ExampleWorkflow::class.java, workflowOptions)
        
        val resultFuture = WorkflowClient.execute(workflowStub::createSecretKey, length)
        val result = resultFuture.get()

        assertEquals(responseHex, result)
    }

    private fun ByteArray.toHexString(): String {
        return joinToString("") { "%02x".format(it) }
    }
}
