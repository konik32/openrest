package orest.validation;


import lombok.Data;

public class UpdateValidationContext {

	private Object dto;
	private Object entity;
	public Object getDto() {
		return dto;
	}
	public void setDto(Object dto) {
		this.dto = dto;
	}
	public Object getEntity() {
		return entity;
	}
	public void setEntity(Object entity) {
		this.entity = entity;
	}
}
