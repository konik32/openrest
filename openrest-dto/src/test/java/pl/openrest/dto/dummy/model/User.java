package pl.openrest.dto.dummy.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User{
	public User() {}
	
	private String name;

	private String surname;

	
	private boolean active;

}
