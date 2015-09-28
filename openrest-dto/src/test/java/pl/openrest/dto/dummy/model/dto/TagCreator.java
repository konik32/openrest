package pl.openrest.dto.dummy.model.dto;

import pl.openrest.dto.dummy.model.Tag;
import pl.openrest.dto.mapper.CreateMapper;
import pl.openrest.dto.registry.DtoInformation;

public class TagCreator implements CreateMapper<Tag, TagDto> {

    @Override
    public Tag create(TagDto from, DtoInformation dtoInfo) throws IllegalStateException {
        Tag tag = new Tag();
        tag.setName("#" + from.getName());
        return tag;
    }

}
