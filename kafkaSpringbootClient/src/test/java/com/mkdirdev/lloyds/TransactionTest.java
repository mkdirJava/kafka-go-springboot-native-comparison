package com.mkdirdev.lloyds;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.mkdirdev.lloyds.business.service.Service;
import com.mkdirdev.lloyds.db.AppTable;
import com.mkdirdev.lloyds.db.ApplicationRepository;

@SpringBootTest
class LloydsApplicationTests2 {

	@Autowired
	private Service service;

	@Autowired
	private ApplicationRepository repo;

	@Test
	void testTransactionRollbackRuntimeExcption() {
		try {
			service.doBusinessActionFunc((repo) -> {
				repo.save(new AppTable());
				throw new RuntimeException();
			});
		} catch (Exception e) {
			// catch here due to throwing runtime exception in test
		}
		assertEquals(0, repo.count());
	}

	@Test
	void testTransaction() {
		try {
			service.doBusinessActionFunc((repo) -> {
				repo.save(new AppTable());
			});
		} catch (Exception e) {
			// catch here due to throwing runtime exception in test
		}

		assertEquals(1, repo.count());
	}

	@Test
	void testTransactionNoRollbackCheckedExcption() {
		try {
			service.doBusinessActionExceptionFunc((repo) -> {
				repo.save(new AppTable());
			});
		} catch (Exception e) {
			// catch here due to throwing runtime exception in test
		}
		assertEquals(1, repo.count());
	}

}
