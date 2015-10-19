package pl.openrest.generator.commons.entity;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.reflections.Reflections;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import pl.openrest.generator.commons.Configuration;

@RunWith(MockitoJUnitRunner.class)
public class DefaultEntityInformationRegistryTest {

    @Mock
    private Reflections reflections;

    @Mock
    private Configuration configuration;
    private DefaultEntityInformationRegistry registry;

    @Before
    public void setUp() {
        Mockito.when(configuration.get("reflections")).thenReturn(reflections);
        Mockito.when(configuration.get("entityAnnotations")).thenReturn(Arrays.asList(Entity.class));
        registry = new DefaultEntityInformationRegistry();
        registry.setConfiguration(configuration);
    }

    @Test
    public void shouldRegisterUserEntityWithoutRepositoryAnnotations() throws Exception {
        // given
        Mockito.doReturn(Collections.singleton(UserEntity.class)).when(reflections).getTypesAnnotatedWith(Entity.class);
        // when
        registry.afterPropertiesSet();
        // then
        EntityInformation entityInfo = registry.get(UserEntity.class);
        Assert.assertFalse(entityInfo.isExported());
        Assert.assertNull(entityInfo.getExcerptProjectionType());
        Assert.assertEquals(UserEntity.class, entityInfo.getEntityType());
        Assert.assertEquals("userEntities", entityInfo.getPath());
    }

    @Test
    public void shouldRegisterUserEntityWithRepositoryRestResource() throws Exception {
        // given
        Mockito.doReturn(Collections.singleton(UserEntity.class)).when(reflections).getTypesAnnotatedWith(Entity.class);
        Mockito.doReturn(Collections.singleton(UserEntityRepository.class)).when(reflections)
                .getTypesAnnotatedWith(RepositoryRestResource.class, true);
        // when
        registry.afterPropertiesSet();
        // then
        EntityInformation entityInfo = registry.get(UserEntity.class);
        Assert.assertTrue(entityInfo.isExported());
        Assert.assertEquals(String.class, entityInfo.getExcerptProjectionType());
        Assert.assertEquals("users", entityInfo.getPath());
    }

    @Test
    public void shouldRegisterUserEntityWithRestResource() throws Exception {
        // given
        Mockito.doReturn(Collections.singleton(UserEntity.class)).when(reflections).getTypesAnnotatedWith(Entity.class);
        Mockito.doReturn(Collections.singleton(UserRestResourceRepository.class)).when(reflections)
                .getTypesAnnotatedWith(RestResource.class, true);
        // when
        registry.afterPropertiesSet();
        // then
        EntityInformation entityInfo = registry.get(UserEntity.class);
        Assert.assertFalse(entityInfo.isExported());
        Assert.assertNull(entityInfo.getExcerptProjectionType());
        Assert.assertEquals("restResourceUsers", entityInfo.getPath());
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(value = TYPE)
    public @interface Entity {

    }

    @Entity
    public static class UserEntity {

    }

    @RepositoryRestResource(path = "users", excerptProjection = String.class)
    public interface UserEntityRepository extends CrudRepository<UserEntity, Long> {

    }

    @RestResource(path = "restResourceUsers", exported = false)
    public interface UserRestResourceRepository extends CrudRepository<UserEntity, Long> {

    }

}
