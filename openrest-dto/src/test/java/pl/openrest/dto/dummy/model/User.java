package pl.openrest.dto.dummy.model;

import lombok.Getter;
import lombok.Setter;

import org.springframework.data.jpa.domain.AbstractPersistable;

@Getter
@Setter
public class User extends AbstractPersistable<Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7439477136548613933L;
	public User() {}
	
	private String name;

	private String surname;

	
	private boolean active;

}
