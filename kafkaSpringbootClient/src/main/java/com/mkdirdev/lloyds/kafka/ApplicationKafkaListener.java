package com.mkdirdev.lloyds.kafka;

import java.time.LocalTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;


@Component
public class ApplicationKafkaListener {
    Logger logger = LoggerFactory.getLogger(ApplicationKafkaListener.class);
    @Autowired Processor processor;
    

    private Integer processedCoutner =0;

    @KafkaListener(id = "${spring.kafka.consumer.group-id}", topics = "${application.topic.input.name}")
    public void listen(
        @Payload List<String> messages, 
        @Header(KafkaHeaders.RECEIVED_KEY) List<String> keys,
        @Header(KafkaHeaders.OFFSET) List<Long> offsets,
        @Header(KafkaHeaders.RECEIVED_PARTITION) List<Long> partitions
        ){
        // logger.info("received the messages %s".formatted(messages));
        try{
            logger.info("Processed %d records time is %s".formatted(processedCoutner,LocalTime.now()));
            processor.process(messages,keys,offsets,partitions);
            processedCoutner = processedCoutner + messages.size(); 
        }catch(Exception e){
             //put on error topic and alert on systems like slack or prometheus for business logic
            logger.error("threw an exception", e); 
        }
    }

}
