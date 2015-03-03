package orest;

import orest.model.dto.TagCreator;
import orest.repository.ExpressionJpaFactoryBean;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ComponentScan
@EnableAutoConfiguration(exclude = { org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration.class })
@EnableJpaRepositories(repositoryFactoryBeanClass = ExpressionJpaFactoryBean.class)
@EntityScan
public class Application  {
	public static void main(String[] args) throws Exception {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public TagCreator tagCreator(){
		return new TagCreator();
	}
}
