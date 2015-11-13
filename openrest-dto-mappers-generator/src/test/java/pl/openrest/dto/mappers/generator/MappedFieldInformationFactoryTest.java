package pl.openrest.dto.mappers.generator;

import java.lang.reflect.Field;
import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.util.ReflectionUtils;

public class MappedFieldInformationFactoryTest {

    @Test
    public void shouldCreateReturnMappedFieldInformation() throws Exception {
        // given
        Field field = ReflectionUtils.findField(TestClass.class, "name");
        // when
        MappedFieldInformation information = MappedFieldInformationFactory.create(field);
        // then
        Assert.assertEquals(MappedFieldInformation.class, information.getClass());
    }
    
    @Test
    public void shouldCreateReturnMappedCollectionFieldInformation() throws Exception {
        // given
        Field field = ReflectionUtils.findField(TestClass.class, "list");
        // when
        MappedFieldInformation information = MappedFieldInformationFactory.create(field);
        // then
        Assert.assertEquals(MappedCollectionFieldInformation.class, information.getClass());
    }

    @Getter
    @Setter
    public static class TestClass {
        private String name;
        private ArrayList<String> list;
    }
}
