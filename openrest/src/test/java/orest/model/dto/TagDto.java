package orest.model.dto;

import lombok.Data;
import orest.dto.Dto;
import orest.dto.Dto.DtoType;
import orest.model.Tag;

@Dto(entityType=Tag.class, entityCreatorType=TagCreator.class, type=DtoType.BOTH)
@Data

public class TagDto {
	
	private String name;
}
