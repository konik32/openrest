package orest.model.projection;

import orest.model.User;

import org.springframework.data.rest.core.config.Projection;

@Projection(name = "standard", types = User.class)
public interface UserProjection {


	String getName();

	String getSurname();
}
