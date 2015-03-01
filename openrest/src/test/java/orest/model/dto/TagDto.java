package orest.model.dto;

import lombok.Data;
import orest.dto.Dto;
import orest.model.Tag;

@Dto(entityType=Tag.class, entityCreatorType=TagCreator.class)
@Data
public class TagDto {

	
	private String name;
}
