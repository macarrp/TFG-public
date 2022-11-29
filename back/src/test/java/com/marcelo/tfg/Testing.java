package com.marcelo.tfg;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class Testing {

	@Test
	void isTestsOk() {
		assertTrue(true, "No funcionan los tests");
	}
}
