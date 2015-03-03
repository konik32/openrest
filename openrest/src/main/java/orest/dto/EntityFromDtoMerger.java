package orest.dto;

public interface EntityFromDtoMerger<R, P> {
	void merge(P from, R entity, DtoInformation dtoInfo);
}
