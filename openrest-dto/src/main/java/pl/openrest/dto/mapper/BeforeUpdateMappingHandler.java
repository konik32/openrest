package pl.openrest.dto.mapper;

public interface BeforeUpdateMappingHandler {
    void handle(Object dto, Object entity);
}
