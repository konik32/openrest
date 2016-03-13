package pl.openrest.dto.mappers.generator;

import java.lang.reflect.Field;
import java.util.List;

import javax.annotation.Generated;
import javax.lang.model.element.Modifier;

import lombok.Getter;
import lombok.Setter;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldFilter;

import pl.openrest.dto.annotations.Dto;
import pl.openrest.dto.mapper.CreateMapper;
import pl.openrest.dto.mapper.Default;
import pl.openrest.dto.mapper.MapperDelegator;
import pl.openrest.dto.mapper.UpdateMapper;
import pl.openrest.dto.mappers.generator.utils.CodeBlockUtils;
import pl.openrest.dto.registry.DtoType;
import pl.openrest.generator.commons.Configuration;
import pl.openrest.generator.commons.RemoteClassNamingStrategy;
import pl.openrest.generator.commons.type.TypeFileWriter;
import pl.openrest.generator.commons.utils.JavaPoetTestUtils;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ CodeBlockUtils.class, MappedFieldPairFactory.class })
public class DtoMapperResolverTest {

    @Mock
    private RemoteClassNamingStrategy namingStrategy;

    @Mock
    private TypeFileWriter typeFileWriter;

    @Mock
    private FieldFilter fieldFilter;

    private Configuration configuration = new Configuration();

    private DtoMapperResolver resolver;

    @Before
    public void setUp() {
        configuration.put("defaultNamingStrategy", namingStrategy);
        configuration.put("typeFileWriter", typeFileWriter);

        Mockito.when(namingStrategy.getClassName(Mockito.anyString())).thenReturn("TestMapper");
        Mockito.when(namingStrategy.getPackageName(Mockito.anyString())).thenReturn("pl.openrest.dto.mappers.test");

        Mockito.when(fieldFilter.matches(Mockito.any(Field.class))).thenReturn(true);
        PowerMockito.mockStatic(CodeBlockUtils.class, Mockito.CALLS_REAL_METHODS);

        MappedFieldPair namePair = Mockito.mock(MappedFieldPair.class);
        MappedFieldPair listPair = Mockito.mock(MappedFieldPair.class);
        MappedFieldPair childPair = Mockito.mock(MappedFieldPair.class);

        Mockito.when(namePair.toCreateCodeBlock()).thenReturn(JavaPoetTestUtils.getSingleStatementCodeBlock("name"));
        Mockito.when(listPair.toCreateCodeBlock()).thenReturn(JavaPoetTestUtils.getSingleStatementCodeBlock("list"));
        Mockito.when(childPair.toCreateCodeBlock()).thenReturn(JavaPoetTestUtils.getSingleStatementCodeBlock("child"));
        Mockito.when(namePair.toUpdateCodeBlock()).thenReturn(JavaPoetTestUtils.getSingleStatementCodeBlock("name"));
        Mockito.when(listPair.toUpdateCodeBlock()).thenReturn(JavaPoetTestUtils.getSingleStatementCodeBlock("list"));
        Mockito.when(childPair.toUpdateCodeBlock()).thenReturn(JavaPoetTestUtils.getSingleStatementCodeBlock("child"));

        PowerMockito.mockStatic(MappedFieldPairFactory.class);
        Mockito.when(MappedFieldPairFactory.create(Mockito.eq(ReflectionUtils.findField(TestDto.class, "name")), Mockito.any(Field.class)))
                .thenReturn(namePair);
        Mockito.when(MappedFieldPairFactory.create(Mockito.eq(ReflectionUtils.findField(TestDto.class, "list")), Mockito.any(Field.class)))
                .thenReturn(listPair);
        Mockito.when(MappedFieldPairFactory.create(Mockito.eq(ReflectionUtils.findField(TestDto.class, "child")), Mockito.any(Field.class)))
                .thenReturn(childPair);

        resolver = new DtoMapperResolver(fieldFilter);
        resolver.setConfiguration(configuration);
    }

    @Test
    public void shouldResolveCallNamingStrategy() throws Exception {
        // given
        // when
        resolver.resolve(TestDto.class);
        // then
        Mockito.verify(namingStrategy).getClassName("TestDto");
        Mockito.verify(namingStrategy).getPackageName("pl.openrest.dto.mappers.generator");
    }

    @Test
    public void shouldResolveCreateTypeSpecWithTestMapperName() throws Exception {
        // given
        // when
        resolver.resolve(TestDto.class);
        // then
        TypeSpec spec = JavaPoetTestUtils.getTypeSpec(typeFileWriter);
        Assert.assertEquals("TestMapper", spec.name);
    }

    @Test
    public void shouldResolveCreateTypeSpecWithPublicModifier() throws Exception {
        // given
        // when
        resolver.resolve(TestDto.class);
        // then
        TypeSpec spec = JavaPoetTestUtils.getTypeSpec(typeFileWriter);
        Assert.assertThat(spec.modifiers, Matchers.contains(Modifier.PUBLIC));
    }

    @Test
    public void shouldResolveCreateTypeSpecImplementingCreateMapperAndUpdateMapperInterfaces() throws Exception {
        // given
        // when
        resolver.resolve(TestDto.class);
        // then
        TypeSpec spec = JavaPoetTestUtils.getTypeSpec(typeFileWriter);
        Assert.assertThat(spec.superinterfaces, Matchers.contains(
                (TypeName) ParameterizedTypeName.get(CreateMapper.class, TestEntity.class, TestDto.class),
                ParameterizedTypeName.get(UpdateMapper.class, TestEntity.class, TestDto.class)));
    }

    @Test
    public void shouldResolveCreateTypeSpecWithGeneratedAndComponentAnnotation() throws Exception {
        // given
        // when
        resolver.resolve(TestDto.class);
        // then
        TypeSpec spec = JavaPoetTestUtils.getTypeSpec(typeFileWriter);
        Assert.assertEquals(3, spec.annotations.size());
        Assert.assertEquals(TypeName.get(Generated.class), spec.annotations.get(0).type);
        Assert.assertEquals("@javax.annotation.Generated(\"pl.openrest.dto.mappers.generator\")", spec.annotations.get(0).toString());
        Assert.assertEquals(TypeName.get(Component.class), spec.annotations.get(1).type);
        Assert.assertEquals(TypeName.get(Default.class), spec.annotations.get(2).type);
    }

    @Test
    public void shouldResolveCreateTypeSpecWithPrivateFinalMapperDelegatorField() throws Exception {
        // given
        // when
        resolver.resolve(TestDto.class);
        // then
        TypeSpec spec = JavaPoetTestUtils.getTypeSpec(typeFileWriter);
        FieldSpec fieldSpec = JavaPoetTestUtils.getFieldSpec(spec, "mapperDelegator");
        Assert.assertNotNull(fieldSpec);
        Assert.assertThat(fieldSpec.modifiers, Matchers.contains(Modifier.PRIVATE, Modifier.FINAL));
        Assert.assertEquals(TypeName.get(MapperDelegator.class), fieldSpec.type);
    }

    @Test
    public void shouldResolveCreateTypeSpecWithConstructorWithMapperDelegatorParameter() throws Exception {
        // given
        // when
        resolver.resolve(TestDto.class);
        // then
        TypeSpec spec = JavaPoetTestUtils.getTypeSpec(typeFileWriter);
        MethodSpec methodSpec = JavaPoetTestUtils.getMethodSpec(spec, "<init>");
        Assert.assertNotNull(methodSpec);
        Assert.assertEquals(1, methodSpec.parameters.size());
        Assert.assertEquals("mapperDelegator", methodSpec.parameters.get(0).name);
        Assert.assertEquals(TypeName.get(MapperDelegator.class), methodSpec.parameters.get(0).type);
    }

    @Test
    public void shouldResolveCreateTypeSpecWithPublicCreateMethodWithTestDtoParameterThatReturnsTestEntity() throws Exception {
        // given
        // when
        resolver.resolve(TestDto.class);
        // then
        TypeSpec spec = JavaPoetTestUtils.getTypeSpec(typeFileWriter);
        MethodSpec methodSpec = JavaPoetTestUtils.getMethodSpec(spec, "create");
        Assert.assertNotNull(methodSpec);
        Assert.assertThat(methodSpec.modifiers, Matchers.contains(Modifier.PUBLIC));
        Assert.assertEquals(TypeName.get(TestEntity.class), methodSpec.returnType);
        Assert.assertEquals(1, methodSpec.parameters.size());
        Assert.assertEquals("dto", methodSpec.parameters.get(0).name);
        Assert.assertEquals(TypeName.get(TestDto.class), methodSpec.parameters.get(0).type);
    }

    @Test
    public void shouldResolveCreateTypeSpecWithPublicVoidMergeMethodWithTestDtoAndTestEntityParameter() throws Exception {
        // given
        // when
        resolver.resolve(TestDto.class);
        // then
        TypeSpec spec = JavaPoetTestUtils.getTypeSpec(typeFileWriter);
        MethodSpec methodSpec = JavaPoetTestUtils.getMethodSpec(spec, "merge");
        Assert.assertNotNull(methodSpec);
        Assert.assertThat(methodSpec.modifiers, Matchers.contains(Modifier.PUBLIC));
        Assert.assertEquals(TypeName.VOID, methodSpec.returnType);
        Assert.assertEquals(2, methodSpec.parameters.size());
        Assert.assertEquals("dto", methodSpec.parameters.get(0).name);
        Assert.assertEquals("entity", methodSpec.parameters.get(1).name);
        Assert.assertEquals(TypeName.get(TestDto.class), methodSpec.parameters.get(0).type);
        Assert.assertEquals(TypeName.get(TestEntity.class), methodSpec.parameters.get(1).type);
    }

    @Test
    public void shouldCreateMethodContainsTestEntityLocalVariable() throws Exception {
        // given
        PowerMockito.when(CodeBlockUtils.entityVariable(TestEntity.class)).thenReturn(
                JavaPoetTestUtils.getSingleStatementCodeBlock("TestEntity localVariable"));
        // when
        resolver.resolve(TestDto.class);
        // then
        TypeSpec spec = JavaPoetTestUtils.getTypeSpec(typeFileWriter);
        MethodSpec methodSpec = JavaPoetTestUtils.getMethodSpec(spec, "create");
        Assert.assertThat(methodSpec.code.toString(), Matchers.containsString("TestEntity localVariable;"));
    }

    @Test
    public void shouldCreateMethodContainsReturnEntityStatementAtTheEnd() throws Exception {
        // given
        // when
        resolver.resolve(TestDto.class);
        // then
        TypeSpec spec = JavaPoetTestUtils.getTypeSpec(typeFileWriter);
        MethodSpec methodSpec = JavaPoetTestUtils.getMethodSpec(spec, "create");
        Assert.assertThat(methodSpec.code.toString(), Matchers.endsWith("return entity;\n"));
    }

    @Test
    public void shouldCreateMethodContainsMappingLogicForAllFieldPairs() throws Exception {
        // given
        // when
        resolver.resolve(TestDto.class);
        // then
        TypeSpec spec = JavaPoetTestUtils.getTypeSpec(typeFileWriter);
        MethodSpec methodSpec = JavaPoetTestUtils.getMethodSpec(spec, "create");
        Assert.assertThat(methodSpec.code.toString(),
                Matchers.allOf(Matchers.containsString("name;"), Matchers.containsString("list;"), Matchers.containsString("child;")));

    }

    @Test
    public void shouldMergeMethodContainsMappingLogicForAllFieldPairs() throws Exception {
        // given
        // when
        resolver.resolve(TestDto.class);
        // then
        TypeSpec spec = JavaPoetTestUtils.getTypeSpec(typeFileWriter);
        MethodSpec methodSpec = JavaPoetTestUtils.getMethodSpec(spec, "merge");
        Assert.assertThat(methodSpec.code.toString(),
                Matchers.allOf(Matchers.containsString("name;"), Matchers.containsString("list;"), Matchers.containsString("child;")));

    }

    @Test
    public void shouldResolveNotCallMappedFieldPairFactoryCreateForDtoFieldWithoutPair() throws Exception {
        // given
        // when
        resolver.resolve(TestDto.class);
        // then
        PowerMockito.verifyStatic(Mockito.never());
        MappedFieldPairFactory.create(Mockito.eq(ReflectionUtils.findField(TestDto.class, "object")), Mockito.any(Field.class));
    }

    @Test
    public void shouldResolveReturnTestMapperClassName() throws Exception {
        // given
        TypeName typeName = resolver.resolve(TestDto.class);
        // when
        Assert.assertEquals(ClassName.get("pl.openrest.dto.mappers.test", "TestMapper"), typeName);
        // then
    }

    @Dto(entityType = TestEntity.class, type = DtoType.BOTH)
    @Getter
    @Setter
    public static class TestDto {
        private String name;
        private List<String> list;

        private TestDto child;

        private Object object;
    }

    @Getter
    @Setter
    public static class TestEntity {
        private String name;
        private List<String> list;

        private TestEntity child;
    }

}
