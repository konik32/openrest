package pl.openrest.dto.handler.spel;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class DtoEvaluationWrapper {

	private @Getter Object dto;
	private @Getter final Object entity;

	public DtoEvaluationWrapper(Object dto, Object entity) {
		this.dto = dto;
		this.entity = entity;
	}
}
