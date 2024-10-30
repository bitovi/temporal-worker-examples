package main

import (
	"context"
	"crypto/tls"
	"log"
	"os"
	"strconv"
	"time"

	"go.temporal.io/sdk/activity"
	"go.temporal.io/sdk/client"
	"go.temporal.io/sdk/worker"
	"go.temporal.io/sdk/workflow"
)

func CreateTemporalClient() (client.Client, error) {
	port := os.Getenv("TEMPORAL_PORT")
	namespace := os.Getenv("TEMPORAL_NAMESPACE")
	certPath := os.Getenv("TEMPORAL_CERT_PATH")
	keyPath := os.Getenv("TEMPORAL_KEY_PATH")

	var connectionOptions client.ConnectionOptions

	if len(certPath) > 0 && len(keyPath) > 0 {
		certificate, err := tls.LoadX509KeyPair(certPath, keyPath)
		if err != nil {
			log.Fatalln("Unable to load cert and key pair.", err)
			return nil, err
		}
		connectionOptions = client.ConnectionOptions{
			TLS: &tls.Config{
				Certificates: []tls.Certificate{certificate},
			},
		}
	}

	temporalClient, err := client.Dial(client.Options{
		HostPort:          port,
		Namespace:         namespace,
		ConnectionOptions: connectionOptions,
	})

	if err != nil {
		log.Fatalln("Unable to create client", err)
		return nil, err
	}

	return temporalClient, nil
}

func Activity(ctx context.Context, workflowId string) (string, error) {
	logger := activity.GetLogger(ctx)
	logger.Info("Running Activity for", workflowId)

	time.Sleep(time.Second * 10)

	logger.Info("Ran Activity for", workflowId)

	return "Hello World!", nil
}

func Workflow(ctx workflow.Context) (string, error) {
	activityContext := workflow.WithActivityOptions(ctx, workflow.ActivityOptions{
		StartToCloseTimeout: 12 * time.Second,
	})
	workflowId := workflow.GetInfo(ctx).WorkflowExecution.ID
	logger := workflow.GetLogger(ctx)
	logger.Info("Running Workflow for", workflowId)

	var result string
	err := workflow.ExecuteActivity(activityContext, Activity, workflowId).Get(ctx, &result)
	if err != nil {
		logger.Error("SaveRequest failed:", err)
		return "", err
	}

	return result, nil
}

func main() {
	taskQueueName := os.Getenv("TEMPORAL_QUEUE")
	activityConcurrency := GetEnvInt("TEMPORAL_ACTIVITY_CONCURRENCY", 1)
	workerConcurrency := GetEnvInt("TEMPORAL_WORKFLOW_CONCURRENCY", 2)
	temporalClient, err := CreateTemporalClient()

	if err != nil {
		log.Fatalln("Unable to create client!", err)
	}
	defer temporalClient.Close()

	workerOptions := worker.Options{
		OnFatalError: func(err error) {
			log.Println("Worker crashed:", err)
		},
		MaxConcurrentActivityExecutionSize:     activityConcurrency,
		MaxConcurrentWorkflowTaskExecutionSize: workerConcurrency,
	}
	workerInstance := worker.New(temporalClient, taskQueueName, workerOptions)
	workerInstance.RegisterWorkflow(Workflow)
	workerInstance.RegisterActivity(Activity)

	err = workerInstance.Run(worker.InterruptCh())
	if err != nil {
		log.Fatalln("unable to start Worker", err)
	}
}

func GetEnvInt(varName string, defaultValue int) int {
	valueStr := os.Getenv(varName)
	if valueStr == "" {
		return defaultValue
	}

	value, err := strconv.Atoi(valueStr)
	if err != nil {
		return defaultValue
	}

	return value
}
