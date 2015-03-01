package orest.controller;

import static com.jayway.restassured.RestAssured.given;
import orest.Application;
import orest.model.dto.ProductDto;
import orest.model.dto.UserDto;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
public class DtoAwareControllerIntegrationTest {

	
	
	
	@Autowired
	private ObjectMapper halObjectMapper;
	
	@Test
	@DatabaseSetup("classpath:/prePostProducts.xml")
	@ExpectedDatabase("classpath:/postPostProducts.xml")
	public void testIfProductIsPopulated() throws JsonProcessingException{
		given().queryParam("dto", "productDto").contentType("application/json")
		.body("{ \"tempName\":\"AGD\", \"description\":\"lorem\", \"user\":\"/users/1\" ,\"tags\":[{\"name\":\"ELECTRONIC\"}]}").post("/products");
	}
}
