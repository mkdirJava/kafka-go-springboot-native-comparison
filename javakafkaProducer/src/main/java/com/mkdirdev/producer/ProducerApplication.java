package com.mkdirdev.producer;

import java.time.LocalTime;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.core.KafkaTemplate;

@SpringBootApplication
public class ProducerApplication implements CommandLineRunner{

	@Autowired
	private KafkaTemplate<String,String> template;
	@Value(value="${application.topic}")
	private String topic;

	@Value(value = "${sleep.interval.sec}")
	private Integer sleepSeconds;

	public static void main(String[] args) {
		SpringApplication.run(ProducerApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		while (true) {
			var key = getKey();
			var value = getValue();
			
			template.send(new ProducerRecord<String,String>(topic,key,value));
			// Thread.sleep();
		}
	}

	private static String getValue() {
        return String.valueOf(LocalTime.now().toSecondOfDay());
    }

    private static String getKey() {
        return LocalTime.now().toString();
    }


}
