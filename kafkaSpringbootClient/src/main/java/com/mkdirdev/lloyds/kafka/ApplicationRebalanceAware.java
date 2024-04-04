package com.mkdirdev.lloyds.kafka;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.TopicPartition;
import org.springframework.kafka.listener.ConsumerAwareRebalanceListener;

/**
 * In the event of a rebalace and the application is up
 * Note 
 * This would not matter if the application is killed off by the kubernetes cluster
 * In Kubernetes SigKill need to alert the process to store. This maybe a race condition between
 * killing the container and sending the save to the external source
 * 
 */
public class ApplicationRebalanceAware implements ConsumerAwareRebalanceListener{

    // Store the offsets in external system
    @Override
    public void onPartitionsRevokedAfterCommit(Consumer<?, ?> consumer, Collection<TopicPartition> partitions) {
        var partitionToPosition= partitions.stream().collect(Collectors.toMap((p)->p.partition(),(p)->consumer.position(p)));
        store(partitionToPosition);
    }

    private void store(Map<Integer, Long> partitionToPosition) {
        // store in external, Key value pair would be good like Redis
    }
}
