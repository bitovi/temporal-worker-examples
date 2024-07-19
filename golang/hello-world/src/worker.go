package main

import (
	"context"
	"crypto/tls"
	"log"
	"os"
	"reflect"
	"strconv"
	"time"

	"go.temporal.io/sdk/activity"
	"go.temporal.io/sdk/client"
	"go.temporal.io/sdk/worker"
	"go.temporal.io/sdk/workflow"
)

func CreateTemporalClient() (client.Client, error) {
	port := os.Getenv("TEMPORAL_ADDRESS")
	namespace := os.Getenv("TEMPORAL_NAMESPACE")
	certPath := os.Getenv("TEMPORAL_CERT")
	keyPath := os.Getenv("TEMPORAL_KEY")

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

	time.Sleep(time.Second * 30)

	logger.Info("Ran Activity for", workflowId)

	return "Hello World!", nil
}

func Workflow(ctx workflow.Context) (string, error) {
	activityContext := workflow.WithActivityOptions(ctx, workflow.ActivityOptions{
		StartToCloseTimeout: 10 * time.Minute,
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

func getEnvVar[T any](name string, fallback T) T {
	rawValue := os.Getenv(name)
	var value T

	if rawValue == "" {
		return fallback
	}

	if reflect.TypeOf(fallback).Kind() == reflect.Int {
		intValue, err := strconv.Atoi(rawValue)
		if err != nil {
			log.Fatalf("Unable to parse $%s=%s as integer: %v", name, rawValue, err)
		}
		value = any(intValue).(T)
	}
	return value
}

func main() {
	taskQueueName := os.Getenv("TEMPORAL_QUEUE")
	maxConcurrentActivities := getEnvVar("TEMPORAL_MAX_CONCURRENT_ACTIVITIES", 0)
	maxConcurrentWorkflows := getEnvVar("TEMPORAL_MAX_CONCURRENT_WORKFLOWS", 0)
	temporalClient, err := CreateTemporalClient()

	if err != nil {
		log.Fatalln("Unable to create client!", err)
	}
	defer temporalClient.Close()

	workerOptions := worker.Options{
		OnFatalError: func(err error) {
			log.Println("Worker crashed:", err)
		},
	}
	if maxConcurrentActivities != 0 {
		workerOptions.MaxConcurrentActivityExecutionSize = maxConcurrentActivities
	}
	if maxConcurrentWorkflows != 0 {
		workerOptions.MaxConcurrentWorkflowTaskExecutionSize = maxConcurrentWorkflows
	}

	workerInstance := worker.New(temporalClient, taskQueueName, workerOptions)
	workerInstance.RegisterWorkflow(Workflow)
	workerInstance.RegisterActivity(Activity)

	err = workerInstance.Run(worker.InterruptCh())
	if err != nil {
		log.Fatalln("unable to start Worker", err)
	}
}
