package main

import (
	"fmt"
	"os"
	"time"

	"github.com/confluentinc/confluent-kafka-go/kafka"
)

func main() {
	processedMessageCounter := 0
	consumer := getConnection()
	consumer.Subscribe(getEnv("TOPIC"), nil)
	for {
		event := consumer.Poll(1000)
		switch event.(type) {
		case *kafka.Message:
			processedMessageCounter++
			println(fmt.Sprintf("processed messages %d finished at %s", processedMessageCounter, time.Now()))
		}
	}
}

func getConnection() *kafka.Consumer {
	if consumer, connectionErr := kafka.NewConsumer(&kafka.ConfigMap{
		"bootstrap.servers": getEnv("BOOTSTRAP_SERVERS"),
		"group.id":          getEnv("GROUP_ID"),
		"auto.offset.reset": "largest"}); connectionErr != nil {
		panic(connectionErr)
	} else {
		return consumer
	}
}

func getEnv(envVar string) string {
	foundVar := os.Getenv(envVar)
	if len(foundVar) == 0 {
		panic(fmt.Errorf("environment variable %s is needed", envVar))
	} else {
		return foundVar
	}
}
