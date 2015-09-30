package pl.openrest.dto.registry;

import lombok.Getter;
import pl.openrest.dto.annotations.Dto;

@Getter
public class DtoInformation {

    private final Class<?> entityType;
    private final String name;
    private final Class<?> dtoType;
    private final DtoType type;

    public DtoInformation(Class<?> dtoType, Dto dtoAnn) {
        this.entityType = dtoAnn.entityType();
        this.name = dtoAnn.name();
        this.dtoType = dtoType;
        this.type = dtoAnn.type();
    }

}
