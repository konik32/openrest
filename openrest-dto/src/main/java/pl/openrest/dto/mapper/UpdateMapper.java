package pl.openrest.dto.mapper;

import pl.openrest.dto.registry.DtoInformation;

/**
 * This interface should be implemented if there is a need for more control over merging entity with dto, than simply mapping field by field
 * as in {@link DefaultCreateAndUpdateMapper}
 * 
 * @author Szymon Konicki
 *
 * @param <R>
 *            Entity type
 * @param <P>
 *            Dto type
 */
public interface UpdateMapper<R, P> {
    void merge(P from, R entity, DtoInformation dtoInfo);
}
