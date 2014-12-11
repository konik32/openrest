package openrest.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;

@Configuration
@ComponentScan
@EnableAutoConfiguration
@EntityScan
public class Application extends OpenRestConfiguration{
	public static void main(String[] args) throws Exception {
		SpringApplication.run(Application.class, args);
	}
	
	@Override
	public RepositoryRestConfiguration config() {
		// TODO Auto-generated method stub
		return super.config().useHalAsDefaultJsonMediaType(false);
	}
	
	
}
