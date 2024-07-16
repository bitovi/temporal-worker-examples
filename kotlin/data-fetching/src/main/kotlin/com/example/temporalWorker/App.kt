package com.example.temporalWorker

import io.temporal.worker.WorkerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.WebApplicationType
import org.springframework.boot.runApplication
import org.slf4j.LoggerFactory

@SpringBootApplication
class TemporalWorker

fun main(args: Array<String>) {

    val logger = LoggerFactory.getLogger(TemporalWorker::class.java)
    logger.info("Starting Temporal Worker...")

    val context = runApplication<TemporalWorker>(*args) {
        setWebApplicationType(WebApplicationType.NONE)
    }
    val workerFactory = context.getBean(WorkerFactory::class.java)
    workerFactory.start()
}
