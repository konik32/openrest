package pl.openrest.dto.event;

import lombok.Getter;

import org.springframework.data.rest.core.event.RepositoryEvent;


public class RepositoryWithDtoEvent extends RepositoryEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2751378023079426945L;
	private @Getter final Object dto;

	public RepositoryWithDtoEvent(Object source, Object dto) {
		super(source);
		this.dto = dto;
	}

}
