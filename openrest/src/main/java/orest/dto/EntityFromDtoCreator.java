package orest.dto;

public interface EntityFromDtoCreator<R,P> {

	R create(P from, DtoInformation dtoInfo)throws IllegalStateException ;
}
