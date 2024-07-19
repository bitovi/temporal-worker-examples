package com.example.temporalWorker

import com.example.temporalWorker.activities.ActivitiesImpl
import io.temporal.client.WorkflowClient
import io.temporal.client.WorkflowClientOptions
import io.temporal.serviceclient.WorkflowServiceStubsOptions
import io.temporal.serviceclient.WorkflowServiceStubs
import io.temporal.worker.WorkerFactory
import io.temporal.worker.Worker
import java.nio.file.Paths
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder

object WorkerApp {
    @JvmStatic
    fun main() {
        val serverAddress = System.getenv("TEMPORAL_ADDRESS")
        val clientCertPath = System.getenv("TEMPORAL_CERT")
        val clientKeyPath = System.getenv("TEMPORAL_KEY")
        val namespace = System.getenv("TEMPORAL_NAMESPACE")
        val queue = System.getenv("TEMPORAL_QUEUE")

        val serviceOptionsBuilder = WorkflowServiceStubsOptions.newBuilder().setTarget(serverAddress)

        if (clientCertPath != null && clientKeyPath != null) {
            // Create SSL context with mTLS certificates
            val sslContext = GrpcSslContexts.forClient()
                .keyManager(Paths.get(clientCertPath).toFile(), Paths.get(clientKeyPath).toFile())
                .build()

            // Configure mTLS
            serviceOptionsBuilder.setSslContext(sslContext)
        }

        // Build the service stubs options
        val serviceOptions = serviceOptionsBuilder.build()

        // Create a Temporal service client
        val service = WorkflowServiceStubs.newServiceStubs(serviceOptions)

        val clientConfig = WorkflowClientOptions.newBuilder().setNamespace(namespace).build()

        // Create a WorkflowClient
        val client = WorkflowClient.newInstance(service, clientConfig)

        // Create a WorkerFactory
        val factory = WorkerFactory.newInstance(client)

        // Create a Worker that listens on a task queue
        val worker: Worker = factory.newWorker(queue)

        // Register the Workflow implementation with the Worker
        worker.registerWorkflowImplementationTypes(WorkflowImpl::class.java)

        // Register the Activity implementation with the Worker
        worker.registerActivitiesImplementations(ActivitiesImpl())

        // Start the Worker
        factory.start()
    }
}
