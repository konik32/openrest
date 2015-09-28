package pl.openrest.dto.mapper;

import pl.openrest.dto.registry.DtoInformation;

public class MapperDelegator implements CreateMapper<Object, Object>, UpdateMapper<Object, Object> {

    private final MapperFactory mapperFactory;

    public MapperDelegator(MapperFactory mapperFactory) {
        this.mapperFactory = mapperFactory;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void merge(Object from, Object entity, DtoInformation dtoInfo) {
        mapperFactory.getUpdateMapper(from.getClass()).merge(from, entity, dtoInfo);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object create(Object from, DtoInformation dtoInfo) {
        return mapperFactory.getCreateMapper(from.getClass()).create(from, dtoInfo);
    }

}
