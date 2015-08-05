package orest.dto.handler;

import java.util.ArrayList;
import java.util.List;

public class DtoHandlerManager implements DtoHandler {
	
	private List<DtoHandler> dtoHandlers = new ArrayList<DtoHandler>();

	@Override
	public void handle(Object dto) {
		for(DtoHandler handler: dtoHandlers)
			handler.handle(dto);	
	}

	@Override
	public void handle(Object dto, Object entity) {
		for(DtoHandler handler: dtoHandlers)
			handler.handle(dto,entity);
	}
	
	public void addHandler(DtoHandler dtoHandler){
		dtoHandlers.add(dtoHandler);
	}
}
