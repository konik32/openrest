package pl.openrest.dto.validation.handler;

import org.springframework.beans.factory.annotation.Autowired;

import pl.openrest.dto.mapper.BeforeCreateMappingHandler;
import pl.openrest.dto.mapper.BeforeUpdateMappingHandler;

public class ValidationContextHandler implements BeforeCreateMappingHandler, BeforeUpdateMappingHandler {

    @Autowired
    private ValidationContext context;

    @Override
    public void handle(Object dto) {
        handle(dto, null);

    }

    @Override
    public void handle(Object dto, Object entity) {
        context.setDto(dto);
        context.setEntity(entity);
    }

}
