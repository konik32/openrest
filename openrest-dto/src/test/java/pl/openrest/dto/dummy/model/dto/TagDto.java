package pl.openrest.dto.dummy.model.dto;

import lombok.Data;
import pl.openrest.dto.annotations.Dto;
import pl.openrest.dto.dummy.model.Tag;
import pl.openrest.dto.registry.DtoType;

@Dto(entityType = Tag.class, type = DtoType.BOTH)
@Data
public class TagDto {

    private String name;
}
