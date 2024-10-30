package main

import (
	"crypto/tls"
	"log"
	"os"

	"go.temporal.io/sdk/client"
)

func CreateTemporalClient() (client.Client, error) {
	port := os.Getenv("TEMPORAL_HOST_PORT")
	namespace := os.Getenv("TEMPORAL_NAMESPACE")
	certPath := os.Getenv("TEMPORAL_CERTIFICATE_PATH")
	keyPath := os.Getenv("TEMPORAL_KEY_PATH")

	tcOptions := client.Options{
		HostPort:  port,
		Namespace: namespace,
	}

	if len(certPath) > 0 {
		cert, err := tls.LoadX509KeyPair(certPath, keyPath)
		if err != nil {
			log.Fatalln("Unable to load cert and key pair.", err)
			return nil, err
		}
		co := client.ConnectionOptions{
			TLS: &tls.Config{Certificates: []tls.Certificate{cert}},
		}
		tcOptions.ConnectionOptions = co
	}

	temporalClient, err := client.Dial(tcOptions)

	if err != nil {
		log.Fatalln("Unable to create client", err)
		return nil, err
	}

	return temporalClient, nil
}
