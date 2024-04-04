package com.mkdirdev.lloyds.business.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.mkdirdev.lloyds.db.ApplicationRepository;

/*
 * Example Service showing
 *  1. Use Memosation technique to cache high cpu idopotent method calls
 *  2. Expose the memosation service call as a 
 *        function
 *  would need to create a memosation for multi arg methods 
 * 
 */
@Component
public class Service {

    @Autowired
    private ApplicationRepository repo;

    // Handle transactions in a repeatable way
    // Postgres is Isolation.READ_COMMITTED by default
    // Becarful rollbacks are only for errors and runtime exceptions
    // Believe checked exceptions are committed
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void doBusinessAction(){
        repo.count();
    }

    @Transactional
    public void doBusinessActionFunc(Consumer<ApplicationRepository> c) throws Exception{
        c.accept(repo);
    }

    @Transactional
    public void doBusinessActionExceptionFunc(Consumer<ApplicationRepository> c) throws Exception{
        c.accept(repo);
        throw new Exception();
    }

    public Callable<BusinessAsyncResult> getDetail(String id) throws InterruptedException {
        // Do some http calls or other io loads
        // Thread.sleep(1000);
        return ()->{
          return new BusinessAsyncResult(id, BigDecimal.valueOf(new Random().nextLong()));
        };
    }


    public Function<Long, Integer> doSomethingHeavyAndIdempotentMemo = getMemorised(
            Service::doSomethingHeavyAndIdempotent);

    protected <T, U> Function<T, U> getMemorised(Function<T, U> f) {
        var records = new HashMap<T, U>();
        // var records = getRedisCache();
        return (T t) -> {
            if (records.containsKey(t)) {
                return records.get(t);
            } else {
                var result = f.apply(t);
                records.put(t, result);
                return result;
            }
        };
    }

    private static Integer doSomethingHeavyAndIdempotent(Long id) {
        try {
            Thread.sleep(3000l);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return 1;
    }
}
