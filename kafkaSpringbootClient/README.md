Spring Boot Kafka

---

To build with a a JVM 

    ./mvnw spring-boot:build-image -DskipTests

To Run nativley

    docker build --no-cache -t spring_kafka_native .