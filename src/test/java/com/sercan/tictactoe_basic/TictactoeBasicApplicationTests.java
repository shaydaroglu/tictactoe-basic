package com.sercan.tictactoe_basic;

import com.sercan.tictactoe_basic.common.TestLoggerExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@ExtendWith(TestLoggerExtension.class)
class TictactoeBasicApplicationTests {

	@Test
	@DisplayName("loads the Spring application context")
	void contextLoads() {
	}

}
