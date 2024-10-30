package main

import (
	"encoding/json"
	"errors"
	"log"
	"net/http"
	"os"

	"github.com/google/uuid"
	"go.temporal.io/sdk/client"
)

type SolicitPaymentRequest = struct {
	UserId string  `json:"userId"`
	Amount float64 `json:"amount"`
}

type SolicitPaymentResponse = struct {
	PaymentRequestId string `json:"paymentRequestId"`
}

type AnswerPaymentRequest = struct {
	Accepted         bool   `json:"accepted"`
	PaymentRequestId string `json:"paymentRequestId"`
}

type AnswerPaymentResponse = struct {
	PaymentId string `json:"paymentId"`
}

type HTTPRequestHandler = func(w http.ResponseWriter, r *http.Request)

const taskQueueName = "payment_request_queue"

func initiatePaymentRequestHandler(temporalClient client.Client) HTTPRequestHandler {
	return func(w http.ResponseWriter, r *http.Request) {
		if r.Method == "POST" {
			var requestBody SolicitPaymentRequest
			err := json.NewDecoder(r.Body).Decode(&requestBody)
			if err != nil {
				http.Error(w, err.Error(), http.StatusBadRequest)
				return
			}

			paymentRequestId := "req_" + uuid.New().String()

			// This starts a new worker so we throw it in a goroutine
			go CreatePaymentRequest(temporalClient, paymentRequestId, requestBody.UserId, requestBody.Amount)

			responseBody := SolicitPaymentResponse{paymentRequestId}
			w.Header().Set("Content-Type", "application/json")
			w.WriteHeader(http.StatusCreated)
			err = json.NewEncoder(w).Encode(&responseBody)
			if err != nil {
				http.Error(w, err.Error(), http.StatusInternalServerError)
				return
			}
		} else {
			http.Error(w, "Method Not Allowed", http.StatusMethodNotAllowed)
		}
	}
}

func paymentResponseHandler(temporalClient client.Client) HTTPRequestHandler {
	return func(w http.ResponseWriter, r *http.Request) {
		if r.Method == "POST" {
			var requestBody AnswerPaymentRequest
			err := json.NewDecoder(r.Body).Decode(&requestBody)
			if err != nil {
				http.Error(w, err.Error(), http.StatusBadRequest)
				return
			}

			paymentUpdate, err := UpdatePaymentRequest(temporalClient, requestBody.PaymentRequestId, UpdateInput{Accepted: requestBody.Accepted})
			if err != nil {
				http.Error(w, err.Error(), http.StatusInternalServerError)
				return
			}

			response := AnswerPaymentResponse{PaymentId: paymentUpdate.PaymentId}
			w.Header().Set("Content-Type", "application/json")
			w.WriteHeader(http.StatusCreated)
			err = json.NewEncoder(w).Encode(&response)
			if err != nil {
				http.Error(w, err.Error(), http.StatusInternalServerError)
				return
			}
		} else {
			http.Error(w, "Method Not Allowed", http.StatusMethodNotAllowed)
		}
	}
}

func main() {
	httpPort := os.Getenv("HTTP_PORT")
	if httpPort == "" {
		log.Fatalln("HTTP_PORT is required!")
	}
	temporalClient, err := CreateTemporalClient()

	if err != nil {
		log.Fatalln("Unable to create client!", err)
	}
	defer temporalClient.Close()

	http.HandleFunc("/payment-request", initiatePaymentRequestHandler(temporalClient))
	http.HandleFunc("/payment-response", paymentResponseHandler(temporalClient))

	log.Printf("HTTP server listening on %s", httpPort)
	err = http.ListenAndServe(":"+httpPort, nil)
	if !errors.Is(err, http.ErrServerClosed) {
		log.Fatalln("Failed to start HTTP server!", err)
	}
}
