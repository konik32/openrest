package orest.dto;
/**
 * This interface should be implemented if there is a need for more control over
 * merging entity with dto, than simply mapping field by field as in
 * {@link DefaultEntityFromDtoCreatorAndMerger}
 * 
 * @author Szymon Konicki
 *
 * @param <R>
 *            Entity type
 * @param <P>
 *            Dto type
 */
public interface EntityFromDtoMerger<R, P> {
	void merge(P from, R entity, DtoInformation dtoInfo);
}
