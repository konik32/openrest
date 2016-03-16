package pl.openrest.dto.handler;

import org.springframework.beans.factory.annotation.Autowired;

import pl.openrest.dto.handler.BeforeCreateMappingHandler;
import pl.openrest.dto.handler.BeforeUpdateMappingHandler;

public class DtoRequestContextHandler implements BeforeCreateMappingHandler, BeforeUpdateMappingHandler {

    @Autowired
    private DtoRequestContext context;

    @Override
    public void handle(Object dto) {
        context.setNew(true);
        context.setDto(dto);
    }

    @Override
    public void handle(Object dto, Object entity) {
        context.setDto(dto);
        context.setEntity(entity);
        context.setNew(false);
    }

}
