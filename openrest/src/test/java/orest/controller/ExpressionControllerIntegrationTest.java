package orest.controller;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import orest.Application;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
@DatabaseSetup("classpath:/products.xml")
public class ExpressionControllerIntegrationTest {


	@Test
	public void doesControllerReturnNotFoundOnUserResourceFilteredByStaticFilter() {
		given().param("orest").when().get("/users/2").then().assertThat().statusCode(404);
	}

	@Test
	public void doesExpandProductOnSingleResource() {
		given().param("orest").param("expand", "user").when().get("/products/1").then().assertThat()
				.body("_embedded.user", notNullValue());
	}

	@Test
	public void doesExpandProductOnCollection() {
		given().param("orest").param("expand", "user").when().get("/products").then().assertThat()
				.body("_embedded.products[0]._embedded.user", notNullValue());
	}

	@Test
	public void doesControllerAddDynamicFilter() {
		given().param("orest").param("filters", "productionYearBetween(3;5)").when().get("/products").then()
				.assertThat().body("page.totalElements", equalTo(3));
	}

	@Test
	public void doesControllerResponseToSearchMethodWithFilters() {
		given().param("orest").param("filters", "productionYearBetween(;5);and;tagIdEq(1)").when()
				.get("/products/search/userIdEq(1)").then().assertThat().body("page.totalElements", equalTo(4));
		given().param("orest").param("filters", "productionYearBetween(5;)").when().get("/products/search/userIdEq(0)")
				.then().assertThat().body("page.totalElements", equalTo(0));
	}

	@Test
	public void doesControllerReturnNonPagedCollection() {
		given().param("orest").when().get("/products/search/nameLike(AGD)").then().assertThat()
				.body("page", nullValue()).and().body("_embedded.products", hasSize(22));
	}
	
//	@Test
//	public void doesControllerSort() {
//		given().param("orest").param("sort", "tagsSize,asc").when().get("/products").prettyPrint();
//	}

}
