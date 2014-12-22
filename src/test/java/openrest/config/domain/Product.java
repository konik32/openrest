package openrest.config.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;


import org.springframework.data.jpa.domain.AbstractPersistable;

import lombok.Getter;
import lombok.Setter;

@Entity
public class Product extends AbstractPersistable<Long> {/**
	 * 
	 */
	private static final long serialVersionUID = 2395145150186163602L;
	
	private @Getter @Setter String name;
	private @Getter @Setter Double price;
	
	@ManyToOne(optional=false, fetch=FetchType.LAZY)
	private @Getter @Setter Category category;
	
	@ManyToOne(optional=false, fetch=FetchType.LAZY)
	private @Getter @Setter User user;
	public Product(){}
	public Product(String name, Double price) {
		super();
		this.name = name;
		this.price = price;
	}
	

}
