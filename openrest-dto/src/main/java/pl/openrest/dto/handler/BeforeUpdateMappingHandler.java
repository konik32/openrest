package pl.openrest.dto.handler;

public interface BeforeUpdateMappingHandler {
    void handle(Object dto, Object entity);
}
