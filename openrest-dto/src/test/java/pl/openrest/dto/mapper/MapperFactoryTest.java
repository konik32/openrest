package pl.openrest.dto.mapper;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;

import pl.openrest.dto.registry.DtoInformation;

@RunWith(MockitoJUnitRunner.class)
public class MapperFactoryTest {

    private MapperFactory mapperFactory;

    @Mock
    private CreateMapper defaultCreateMapper;

    @Mock
    private UpdateMapper defaultUpdateMapper;

    @Mock
    private ApplicationContext applicationContext;

    @Before
    public void setUp() {
        mapperFactory = new MapperFactory();
        mapperFactory.setDefaultCreateMapper(defaultCreateMapper);
        mapperFactory.setDefaultUpdateMapper(defaultUpdateMapper);

    }

    @Test
    public void shouldRegisterMappers() throws Exception {
        // given
        DummyMapper mapper = new DummyMapper();
        Mockito.when(applicationContext.getBeansOfType(CreateMapper.class)).thenReturn(
                Collections.singletonMap("createMapper", (CreateMapper) mapper));
        Mockito.when(applicationContext.getBeansOfType(UpdateMapper.class)).thenReturn(
                Collections.singletonMap("createMapper", (UpdateMapper) mapper));
        // when
        mapperFactory.setApplicationContext(applicationContext);
        // then
        Assert.assertEquals(mapper, mapperFactory.getCreateMapper(Object.class));
        Assert.assertEquals(mapper, mapperFactory.getUpdateMapper(Object.class));
    }

    @Test
    public void shouldReturnDefaultMapperOnNoCustomMapper() throws Exception {
        // given
        // when
        // then
        Assert.assertEquals(defaultCreateMapper, mapperFactory.getCreateMapper(Object.class));
        Assert.assertEquals(defaultUpdateMapper, mapperFactory.getUpdateMapper(Object.class));
    }

    public static class DummyMapper implements CreateMapper<Object, Object>, UpdateMapper<Object, Object> {

        @Override
        public Object create(Object from, DtoInformation dtoInfo) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void merge(Object from, Object entity, DtoInformation dtoInfo) {
            // TODO Auto-generated method stub

        }

    }
}
