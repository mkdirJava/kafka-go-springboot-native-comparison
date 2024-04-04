Graalvm native build of a kafka producer

To build

    docker build --no-cache -t kafka_producer .

    or
    
    .\mvnw.cmd spring-boot:build-image -DskipTests


Then the producer can be ran up in kubernetes via 

kubectl apply -f <project root>/k8s/kafka_producer.yaml
