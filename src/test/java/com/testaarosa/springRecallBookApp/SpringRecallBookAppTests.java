package com.testaarosa.springRecallBookApp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@AutoConfigureTestDatabase
@TestPropertySource("classpath:application-test.properties")
class SpringRecallBookAppTests {

	@Test
	void contextLoads() {
	}

}
