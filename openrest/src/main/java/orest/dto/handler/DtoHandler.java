package orest.dto.handler;

public interface DtoHandler {

	void handle(Object dto);

	void handle(Object dto, Object entity);
}
