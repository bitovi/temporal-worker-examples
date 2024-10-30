package main

import (
	"context"

	"github.com/google/uuid"
	"go.temporal.io/sdk/activity"
)

func SaveRequest(ctx context.Context, workflowId string, userId string, amount float64) error {
	logger := activity.GetLogger(ctx)
	logger.Info("Creating request record", workflowId, userId, amount)

	// TODO create payment request record here

	return nil
}

func NotifyPayer(ctx context.Context, workflowId string, userId string, amount float64) error {
	logger := activity.GetLogger(ctx)
	logger.Info("Notifying payer", workflowId, userId, amount)

	// TODO send notification to payer here

	return nil
}

func TransferMoney(ctx context.Context, workflowId string, userId string, amount float64) (string, error) {
	logger := activity.GetLogger(ctx)
	logger.Info("Transferring money", workflowId, userId, amount)

	paymentRequestId := "pay_" + uuid.New().String()

	// TODO check balance and transfer funds here

	return paymentRequestId, nil
}
