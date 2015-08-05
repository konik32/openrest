package orest.dto.authorization;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class DtoSecurityWrapper {

	private @Getter final Object dto;
	private @Getter final Object entity;

	public DtoSecurityWrapper(Object dto, Object entity) {
		this.dto = dto;
		this.entity = entity;
	}
}
