package com.example.temporalWorker.config

import com.example.temporalWorker.activities.ActivitiesImpl
import com.example.temporalWorker.workflows.WorkflowImpl
import io.temporal.serviceclient.WorkflowServiceStubs
import io.temporal.serviceclient.WorkflowServiceStubsOptions
import io.temporal.worker.Worker
import io.temporal.worker.WorkerFactory
import io.temporal.client.WorkflowClient
import io.temporal.client.WorkflowClientOptions
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContext

@Configuration
class WorkerConfig(
    private val sslContext: SslContext?,
    private val myActivityImpl: ActivitiesImpl
) {

    @Bean
    fun workflowServiceStubs(): WorkflowServiceStubs {
        val serverAddress = System.getenv("TEMPORAL_ADDRESS")

        val serviceOptionsBuilder = WorkflowServiceStubsOptions.newBuilder()
            .setTarget(serverAddress)

        sslContext?.let {
            serviceOptionsBuilder.setSslContext(it)
        }

        val serviceOptions = serviceOptionsBuilder.build()

        return WorkflowServiceStubs.newServiceStubs(serviceOptions)
    }

    @Bean
    fun workflowClient(workflowServiceStubs: WorkflowServiceStubs): WorkflowClient {
        val namespace = System.getenv("TEMPORAL_NAMESPACE")
        val clientConfig = WorkflowClientOptions.newBuilder().setNamespace(namespace).build()
        return WorkflowClient.newInstance(workflowServiceStubs, clientConfig)
    }

    @Bean
    fun workerFactory(workflowClient: WorkflowClient): WorkerFactory {
        return WorkerFactory.newInstance(workflowClient)
    }

    @Bean
    fun commandLineRunner(workerFactory: WorkerFactory): CommandLineRunner {
        return CommandLineRunner {
            val queue = System.getenv("TEMPORAL_QUEUE")
            val worker: Worker = workerFactory.newWorker(queue)
            worker.registerWorkflowImplementationTypes(WorkflowImpl::class.java)
            worker.registerActivitiesImplementations(myActivityImpl)
            workerFactory.start()
        }
    }
}
