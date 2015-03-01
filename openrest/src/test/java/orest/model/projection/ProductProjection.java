package orest.model.projection;

import orest.model.Product;
import orest.model.User;

import org.springframework.data.rest.core.config.Projection;

@Projection(name = "standard", types = Product.class)
public interface ProductProjection {

	String getName();

	String getDescription();

	Integer getProductionYear();
}
