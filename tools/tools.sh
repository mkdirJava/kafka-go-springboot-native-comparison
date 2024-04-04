#/bin/bash

function createTopic(){
    topicName=$1
    kubectl exec deployments/broker --container kafka-container -- bash -c "/bin/kafka-topics --create --bootstrap-server broker:9092 --partitions 2 --topic $topicName"
    if [ $(kubectl exec deployments/broker --container kafka-container -- bash -c "/bin/kafka-topics --list --bootstrap-server broker:9092" | grep $topicName) ]
    then
        return 
    else
        echo "retrying to create the topic $topicName"
        sleep 5
        createTopic $topicName
    fi
}   

function listTopics(){
    kubectl exec deployments/broker --container kafka-container -- bash -c "/bin/kafka-topics --list --bootstrap-server broker:9092"
}

function setupKube(){
    # setup kubernetes kafka
    kubectl apply -f $home/metrics-server/metrics.yaml

    kubectl apply -f $home/k8s/broker.yaml
    topics=("test-input" "error-output")
    
    for topic in "${topics[@]}"; do
        echo "creating topic $topic"
        createTopic $topic    
    done

    echo "finished setting up kubernetes kafka"
}

function tearDownKube(){
    kubectl delete -f $home/k8s/broker.yaml
    kubectl delete -f $home/metrics-server/metrics.yaml
}

set +eu
home=$(pwd)

function buildAllDockerImages(){
    docker build -t kafka_producer_native $home/javakafkaProducer

    docker build -t kafka_consumer_go  $home/kafkaGoClient
    docker build -t kafka_consumer_spring_native $home/kafkaSpringbootClient
    cd $home/kafkaSpringbootClient && ./mvnw spring-boot:build-image -DskipTests && cd $home
}

function applySender(){
    kubectl apply -f $home/k8s/kafka_producer.yaml
}

function deleteSender(){
    kubectl delete -f $home/k8s/kafka_producer.yaml
}

function scaleProducer(){
    kubectl scale deployment kafka-producer --replicas $1
}

function applyConsumers(){
    kubectl apply -f $home/k8s/kafka_consumer_spring_native.yaml
    kubectl apply -f $home/k8s/kafka_consumer_spring.yaml
    kubectl apply -f $home/k8s/kafka_consumer_go.yaml
}

function deleteConsumers(){
    kubectl delete -f $home/k8s/kafka_consumer_spring_native.yaml
    kubectl delete -f $home/k8s/kafka_consumer_spring.yaml
    kubectl delete -f $home/k8s/kafka_consumer_go.yaml
}

function watchTop(){
    watch kubectl top po 
}

function watchPo(){
    kubectl get po -w
}