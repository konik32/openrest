package orest.model.dto;

import orest.dto.DtoInformation;
import orest.dto.EntityFromDtoCreator;
import orest.model.Tag;

public class TagCreator implements EntityFromDtoCreator<Tag, TagDto> {

	@Override
	public Tag create(TagDto from, DtoInformation dtoInfo) throws IllegalStateException {
		Tag tag = new Tag();
		tag.setName("#" + from.getName());
		return tag;
	}

}
