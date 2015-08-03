package orest.dto;

import lombok.Data;
import orest.dto.Dto.DtoType;

@Data
public class DtoInformation {

	private final Class<?> entityType;
	private final String name;
	private final Class<?> dtoType;
	private final Class<?> entityCreatorType;
	private final Class<?> entityMergerType;
	private final DtoType type;
}
