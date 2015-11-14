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
    public void shouldCreateReturnMappedFieldPair() throws Exception {
        // given
        Field field = ReflectionUtils.findField(TestClass.class, "name");
        // when
        MappedFieldPair information = MappedFieldPairFactory.create(field, field);
        // then
        Assert.assertEquals(MappedFieldPair.class, information.getClass());
    }

    @Test
    public void shouldCreateReturnMappedCollectionFieldPair() throws Exception {
        // given
        Field field = ReflectionUtils.findField(TestClass.class, "list");
        // when
        MappedFieldPair information = MappedFieldPairFactory.create(field, field);
        // then
        Assert.assertEquals(MappedCollectionFieldPair.class, information.getClass());
    }

    @Getter
    @Setter
    public static class TestClass {
        private String name;
        private ArrayList<String> list;
    }
}
