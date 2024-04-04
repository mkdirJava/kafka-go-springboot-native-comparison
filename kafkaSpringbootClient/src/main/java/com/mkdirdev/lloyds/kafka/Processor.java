package com.mkdirdev.lloyds.kafka;

import java.util.List;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class Processor {
    @Value(value="${application.topic.input.threshold}")
    private Integer threshold; 
    @Autowired
	private KafkaTemplate<String,String> kafkaproducer;
    @Value(value = "${application.topic.error}")
    private String errorTopic;
    // @Autowired
    // private Service service;
    // @Autowired
    // private AppCache cache;   
    // private ExecutorService execution = Executors.newFixedThreadPool(10);
    private Logger logger = LoggerFactory.getLogger(Processor.class);
    
    @Async
    public void process(List<String> messages, List<String> keys, List<Long> offsets,List<Long> partitions){
        
        if (messages.size() >= threshold){
            for (var counter = messages.size(); counter < messages.size(); counter ++){
                // var partition = partitions.get(counter);
                // var offset = offsets.get(counter);
                var message = messages.get(counter);
                var key = keys.get(counter);
                // if(!cache.shouldProcess(offset,partition)){
                    // cache.evict(offset,partition);
                //     continue;
                // }else{
                    // do unit of work here
                try{
                    // do unit of work here
                    // Ethos to Quickly and Efficently process records
                    //  * Not to over complecate
                            // Handling where offsets are stored
                            // Rely on consumers having constont connection
                            // In the event of rebalance the quicker a bactch can be 
                            // finished the better the heart beat a consumer can get 
                            // back to the kafka broker

                    //  1. use of database connection pools 
                    //     Spring Data uses HikariCp, 
                    //     Could call procs if needed to offload work to database
                    //  2. Memoised Methods
                    //  3. Obtaining static data once in memory @startup
                    //  4. Use of Async calls 

                    // Hypothetical 
                    // There is a heavy resource method call and a none dependant call too
                    // execute the none depenant code in another thread whist the main is hapening

                    // Need to get the detail for the message, start this and do it in the background
                    // var futureDetail = execution.submit(service.getDetail(message));
                    
                    // Main thread do something here, memo may never seen ths before could cost
                    // var resultantCalculation = service.doSomethingHeavyAndIdempotentMemo.apply(new Random().nextLong());
                    // var foundDetail = futureDetail.get();

                    // can now do something with the resultantCalculation and foundDetail



                }catch(Exception e){
                    logger.info("error happend "+ e.getLocalizedMessage());
                    //put on error topic and alert on systems like slack or prometheus for business logic
                    kafkaproducer.send(new ProducerRecord<String,String>(errorTopic,key, message));
                }
                    // last statement
                    // cache.saveProcessed(offset,partition);
                // }
            } 

        }else{
            var messageMidPoint = messages.size() / 2;
            var threadOne = new Thread(()->process(
                messages.subList(0, messageMidPoint),
                keys.subList(0, messageMidPoint),
                offsets.subList(0, messageMidPoint),
                partitions.subList(0, messageMidPoint)
            ));
            var threadTwo = new Thread(()->process(
                messages.subList(messageMidPoint,messages.size()),
                keys.subList(messageMidPoint,keys.size()),
                offsets.subList(messageMidPoint,offsets.size()),
                partitions.subList(messageMidPoint, partitions.size())
            ));
            try {
                threadOne.join();
                threadTwo.join();
            } catch (InterruptedException e) {
                logger.info("CANNOT HANDLE BATCH, THREAD ERROR "+ e.getLocalizedMessage());
            }
            
            // execution.submit(threadOne);
            // execution.submit(threadTwo);
        }

    }

}
