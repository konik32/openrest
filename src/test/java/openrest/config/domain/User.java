package openrest.config.domain;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import lombok.Data;
import lombok.EqualsAndHashCode;
import openrest.query.StaticFilter;

import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Data
@EqualsAndHashCode
public class User extends AbstractPersistable<Long> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2902271887674358698L;

	private String username;
	private String password;

	@OneToMany(mappedBy = "user",cascade=CascadeType.ALL)
	private List<Product> products;
	public User(){}
	public User(String username, String password, List<Product> products) {
		super();
		this.username = username;
		this.password = password;
		this.products = products;
		for(Product product: products){
			product.setUser(this);
		}
	}

}
