package orest.dto;

/**
 * This interface should be implemented if there is a need for more control over
 * creation of entity from dto, than simply mapping field by field as in
 * {@link DefaultEntityFromDtoCreatorAndMerger}
 * 
 * @author Szymon Konicki
 *
 * @param <R>
 *            Entity type
 * @param <P>
 *            Dto type
 */
public interface EntityFromDtoCreator<R, P> {

	R create(P from, DtoInformation dtoInfo);

}
