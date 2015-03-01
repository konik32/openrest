package orest.dto;

import lombok.Data;

@Data
public class DtoInformation {

	private final Class<?> entityType;
	private final String name;
	private final Class<?> dtoType;
	private final Class<?> entityCreatorType;
}
