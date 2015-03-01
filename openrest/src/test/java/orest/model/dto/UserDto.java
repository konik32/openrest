package orest.model.dto;

import lombok.Data;
import orest.dto.Dto;
import orest.model.User;

import org.springframework.beans.factory.annotation.Value;

@Data
@Dto(entityType=User.class, name="userDto")
public class UserDto {

	private String name;

	private String surname;
	
}
