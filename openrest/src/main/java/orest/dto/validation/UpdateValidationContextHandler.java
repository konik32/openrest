package orest.dto.validation;

import orest.dto.handler.DtoHandler;

import org.springframework.beans.factory.annotation.Autowired;

public class UpdateValidationContextHandler implements DtoHandler {

	@Autowired
	private UpdateValidationContext context;
	
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
