package com.mkdirdev.lloyds.kafka;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * mocked out cache implementation
 * key valyue pair, like Redis
 */
@Component
public class AppCache {

    @Value(value="${spring.kafka.consumer.max-poll-records}")
    private Integer batchSize;

    private Map<Long,List<Long>> paritionToOffset = new HashMap<>();

    public boolean shouldProcess(Long offset, Long partition) {
        return !paritionToOffset.get(partition).contains(offset);
    }

    public void saveProcessed(Long offset, Long partition) {
        // add the new offset
        var list = new ArrayList<Long>();
        list.add(offset);
        paritionToOffset.computeIfAbsent(partition, (k)->list);
        paritionToOffset.computeIfPresent(partition, (key,presentList)-> {
            presentList.add(offset);
            return presentList;
        });
        evict(offset, partition);
    }

    public void evict(Long offset, Long partition) {
        // purge the cache
        // all partitions that have offsets less than (largest - batch size)
        // need to think when to remove the partition itself
        // Also this might be called far too frequently
        paritionToOffset.entrySet().forEach(entry->{
            var largest = entry.getValue().stream().max((a,b)-> a >= b ? 1 : 0 );
            entry.getValue().removeIf(cachedOffset -> cachedOffset < (batchSize+ largest.get()));
        });
    }




    
}
