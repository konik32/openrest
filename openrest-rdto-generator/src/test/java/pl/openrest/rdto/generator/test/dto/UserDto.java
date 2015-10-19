package pl.openrest.rdto.generator.test.dto;

import java.time.LocalDateTime;

import pl.openrest.dto.annotations.Dto;
import pl.openrest.dto.registry.DtoType;

@Dto(entityType = Object.class, name = "userDto", type = DtoType.BOTH)
public class UserDto {

    private final Long id = 1l;
    private boolean active;
    private String name;
    private LocalDateTime created;
}
