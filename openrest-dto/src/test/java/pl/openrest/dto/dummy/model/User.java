package pl.openrest.dto.dummy.model;

import org.springframework.data.annotation.Id;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User{
	public User() {}
	
	@Id
	private Long id;
	
	private String name;

	private String surname;

	
	private boolean active;

}
