package pl.openrest.dto.dummy.model.dto;

import lombok.Data;
import pl.openrest.dto.annotations.Dto;
import pl.openrest.dto.dummy.model.User;
import pl.openrest.dto.registry.DtoType;

@Data
@Dto(entityType = User.class, name = "userDto", type = DtoType.BOTH)
public class UserDto {

    private String name;

    private String surname;

}
