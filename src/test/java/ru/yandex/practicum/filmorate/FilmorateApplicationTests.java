package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
class FilmorateApplicationTests {

	@Autowired
	MockMvc mockMvc;
	@Test
	void contextLoads() {

	}

}
