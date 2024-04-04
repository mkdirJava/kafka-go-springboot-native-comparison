package com.mkdirdev.lloyds;

import java.time.LocalTime;

import org.junit.jupiter.api.Test;

import com.mkdirdev.lloyds.business.service.Service;

// @SpringBootTest
class LloydsApplicationTests {


    private Service unit = new Service();
	// @Test
	// void contextLoads() {
	// }
	@Test
    public void testMemo(){
        var firstCall = secondsTaken(()->unit.doSomethingHeavyAndIdempotentMemo.apply(1L));
        var secondCall = secondsTaken(()->unit.doSomethingHeavyAndIdempotentMemo.apply(1L));
        assert(secondCall < firstCall);
    }

	    private int secondsTaken(Runnable test){
        var startTime = LocalTime.now();
        test.run();
        var finishTime = LocalTime.now();
        return finishTime.getSecond() - startTime.getSecond();
    }

}
