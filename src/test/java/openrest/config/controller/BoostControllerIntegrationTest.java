package openrest.config.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import openrest.config.Application;
import openrest.config.domain.Category;
import openrest.config.domain.CategoryRepository;
import openrest.config.domain.Product;
import openrest.config.domain.ProductRepository;
import openrest.config.domain.User;
import openrest.config.domain.UserRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.WebApplicationContext;

import static com.jayway.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class BoostControllerIntegrationTest {

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Before
	public void setUp() {
		List<Product> products = new ArrayList<Product>();
		for (int i = 0; i < 20; i++) {
			products.add(new Product("product_" + i, new Double(i * 100)));
		}
		Category cat = new Category("category");
		User user = new User("user", "password", products);
		categoryRepository.save(cat);
		cat.setProducts(products);
		userRepository.save(user);
		Product user2Product = new Product("user2_product", new Double(2));
		user2Product.setCategory(cat);
		List<Product> products2 = Arrays.asList(user2Product);
		User user2 = new User("user2", "passowrd", products2);
		userRepository.save(user2);
		

	}

	@Test
	public void doesControllerReturnPropertyCollection() {
		given().param("filter", "between(id,1,10)").param("orest").when().get("/users/1/products").then().assertThat().body("page", notNullValue());
	}
	
	@Test
	public void isStaticFilterFilterCalled(){
		given().param("filter", "between(id,1,10)").param("orest").when().get("/users").then().assertThat().body("page.totalElements", equalTo(0));
	}
	
	
	@Test
	public void doesStaticFilterIgnoreWork(){
		given().param("filter", "between(id,1,10)").param("orest").param("sFilter", "user_filter").when().get("/users").then().assertThat().body("page.totalElements", greaterThan(0));
	}
	
	@Test
	public void isResourceExpanded(){
		given().param("expand", "user").param("orest").when().get("/products/1").then().assertThat().body("_embedded.user", notNullValue());
	}
	
	@Test
	public void doesSpelFilterFilterOutProductNameParameter(){
		given().param("orest").when().get("/products/1").then().assertThat().body("price", notNullValue(), "name", nullValue());
	}
	
	@Test
	public void doesFilterByIdsInUri(){
		given().param("orest").when().get("/users/2/products").then().assertThat().body("page.totalElements", equalTo(1));
	}
	
	
}
