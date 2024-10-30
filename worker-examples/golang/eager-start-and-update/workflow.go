package main

import (
	"context"
	"time"

	"go.temporal.io/sdk/client"
	"go.temporal.io/sdk/workflow"
)

const UpdateName = "your_update_name"

type UpdateInput = struct {
	Accepted bool
}

type PayerResponse = struct {
	ReceivedResponse bool
	Accepted         bool
	PaymentId        string
}

func CreatePaymentRequestWorkflow(ctx workflow.Context, userId string, amount float64) (string, error) {
	activityContext := workflow.WithActivityOptions(ctx, workflow.ActivityOptions{
		StartToCloseTimeout: 10 * time.Second,
	})
	workflowId := workflow.GetInfo(ctx).WorkflowExecution.ID
	logger := workflow.GetLogger(ctx)
	logger.Info("CreatePaymentRequestWorkflow started", userId, amount)

	var response PayerResponse
	err := workflow.SetUpdateHandler(ctx, UpdateName, func(ctx workflow.Context, arg UpdateInput) (PayerResponse, error) {
		if arg.Accepted {
			var paymentRequestId string
			err := workflow.ExecuteActivity(activityContext, TransferMoney, workflowId, userId, amount).Get(ctx, &paymentRequestId)
			if err != nil {
				logger.Error("TransferMoney failed:", err)
				return response, err
			}
			response.PaymentId = paymentRequestId
		}
		response.Accepted = arg.Accepted
		// This needs to happen last or the workflow will exit early
		response.ReceivedResponse = true

		return response, nil
	})

	if err != nil {
		logger.Error("Failed to register update handler:", err)
		return "", err
	}

	err = workflow.ExecuteActivity(activityContext, SaveRequest, workflowId, userId, amount).Get(ctx, nil)
	if err != nil {
		logger.Error("SaveRequest failed:", err)
		return "", err
	}

	err = workflow.ExecuteActivity(activityContext, NotifyPayer, workflowId, userId, amount).Get(ctx, nil)
	if err != nil {
		logger.Error("NotifyPayer failed:", err)
		return "", err
	}

	workflow.Await(ctx, func() bool {
		return response.ReceivedResponse
	})

	return response.PaymentId, nil
}

func UpdatePaymentRequest(temporalClient client.Client, workflowIdString string, updateInput UpdateInput) (PayerResponse, error) {
	var result PayerResponse

	updateHandle, err := temporalClient.UpdateWorkflow(context.Background(), workflowIdString, "", UpdateName, updateInput)
	if err != nil {
		return result, err
	}

	err = updateHandle.Get(context.Background(), &result)
	if err != nil {
		return result, err
	}

	return result, nil
}
