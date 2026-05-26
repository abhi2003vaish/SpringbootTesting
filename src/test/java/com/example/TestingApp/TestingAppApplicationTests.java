package com.example.TestingApp;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.*;
import org.assertj.core.api.Assertions;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

//@SpringBootTest
@Slf4j
class TestingAppApplicationTests {

	@BeforeEach
	void setUp(){
		log.info("Starting the method ,setting up config");
	}

	@AfterEach
	void tearDown(){
		log.info("Tearing down the method");
	}

	@BeforeAll
	static void setUpOnce(){
		log.info("SetUp Once.....");
	}

	@AfterAll
	static void TearDownOnce(){
		log.info("Teardown down all.....");
	}

	@Test
//	@Disabled
	void contextLoads() {
		log.info("Test one is run");
	}

	@Test
	void testNumberOne(){
		int a = 3;
		int b = 5;

		int result = addTwoNumbers(a,b);

//		Assertions.assertEquals(8,result);

//		Assertions.assertThat(result).isEqualTo(8)
//						.isCloseTo(9, Offset.offset(1));
//                     OR
//		assertThat() is static method so can write in this way to import the static method package
		assertThat(result).isEqualTo(8)
				.isCloseTo(9, Offset.offset(1));

//		now we add two assertions if any of the assertion line fail then test get fail
		assertThat("Apple")
				.isEqualTo("Apple")
				.startsWith("pp")
				.endsWith("le")
				.hasSize(5);

		log.info("correct");
	}

	@Test
//	@DisplayName("displayTestNameTwo")
	void testNumberTwo(){
		log.info("Test two is run");
	}


	int addTwoNumbers(int a,int b){
		return a+b;
	}

	@Test
	void testDivideTwoNumbers_WhenDenominatorIsZero_ThenArithmeticException(){
		 int a = 5;
		 int b = 0;

		 assertThatThrownBy(() -> divideTwoNumbers(a,b))
				 .isInstanceOf(ArithmeticException.class)
				 .hasMessage("Tried to divide by zero");
	}

	double divideTwoNumbers(int a,int b){
		try{
			return a/b;
		}catch(ArithmeticException e){
			log.error("Arithmetic Exception occured:"+e.getLocalizedMessage());
			throw new ArithmeticException("Tried to divide by zero");
		}
	}

}
