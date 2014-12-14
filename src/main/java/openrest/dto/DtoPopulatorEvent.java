package openrest.dto;

import java.util.List;

import org.springframework.context.ApplicationEvent;
import org.springframework.hateoas.core.EmbeddedWrapper;

public class DtoPopulatorEvent extends ApplicationEvent {

	private final String[] dtos;
	private final List<EmbeddedWrapper> embeddeds;

	public DtoPopulatorEvent(Object source, List<EmbeddedWrapper> embeddeds, String[] dtos) {
		super(source);
		this.dtos = dtos;
		this.embeddeds = embeddeds;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1616017896639171135L;

	public String[] getDtos() {
		return dtos;
	}

	public List<EmbeddedWrapper> getEmbeddeds() {
		return embeddeds;
	}

}
