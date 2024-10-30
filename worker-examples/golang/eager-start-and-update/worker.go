package main

import (
	"context"
	"log"

	"go.temporal.io/sdk/client"
	"go.temporal.io/sdk/worker"
)

func CreatePaymentRequest(temporalClient client.Client, paymentId string, userId string, amount float64) {
	var result string
	workerOptions := worker.Options{
		OnFatalError: func(err error) {
			log.Println("Worker crashed:", err)
		},
	}
	workerInstance := worker.New(temporalClient, taskQueueName, workerOptions)
	workerInstance.RegisterWorkflow(CreatePaymentRequestWorkflow)
	workerInstance.RegisterActivity(SaveRequest)
	workerInstance.RegisterActivity(NotifyPayer)
	workerInstance.RegisterActivity(TransferMoney)
	err := workerInstance.Start()

	if err != nil {
		log.Printf("Failed to start worker: %v", err)
		return
	}
	defer workerInstance.Stop()

	workflowOptions := client.StartWorkflowOptions{
		ID:               paymentId,
		TaskQueue:        taskQueueName,
		EnableEagerStart: true,
	}

	workflowExecution, err := temporalClient.ExecuteWorkflow(
		context.Background(),
		workflowOptions,
		CreatePaymentRequestWorkflow,
		userId,
		amount,
	)
	if err != nil {
		log.Printf("Failed to execute workflow: %v", err)
		return
	}

	err = workflowExecution.Get(context.Background(), &result)
	if err != nil {
		log.Printf("Failed to get workflow result: %v", err)
		return
	}
}
