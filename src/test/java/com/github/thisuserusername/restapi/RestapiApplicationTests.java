package com.github.thisuserusername.restapi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class RestapiApplicationTests {

    @Autowired
    private ApplicationContext applicationContext;

	@Test
	void contextLoads() {
        assertThat(applicationContext).isNotNull();
	}

}
