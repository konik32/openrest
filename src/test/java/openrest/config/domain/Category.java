package openrest.config.domain;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import org.springframework.data.jpa.domain.AbstractPersistable;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode
public class Category extends AbstractPersistable<Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6131139361106044223L;

	private String name;

	@OneToMany(mappedBy = "category", cascade=CascadeType.ALL)
	private List<Product> products;
	public Category(){}
	public Category(String name) {
		this.name = name;
	}
	
	public void setProducts(List<Product> products){
		this.products = products;
		for (Product product : products) {
			product.setCategory(this);
		}
	}
}
