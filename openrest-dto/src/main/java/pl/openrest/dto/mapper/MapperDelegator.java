package pl.openrest.dto.mapper;


public class MapperDelegator implements CreateMapper<Object, Object>, UpdateMapper<Object, Object> {

    private final MapperFactory mapperFactory;

    public MapperDelegator(MapperFactory mapperFactory) {
        this.mapperFactory = mapperFactory;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void merge(Object from, Object entity) {
        mapperFactory.getUpdateMapper(from.getClass()).merge(from, entity);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object create(Object from) {
        return mapperFactory.getCreateMapper(from.getClass()).create(from);
    }

}
