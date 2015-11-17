package pl.openrest.rdto.generator;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;

import javax.lang.model.element.Modifier;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.util.ReflectionUtils.FieldFilter;

import pl.openrest.dto.annotations.Dto;
import pl.openrest.dto.registry.DtoType;
import pl.openrest.generator.commons.Configuration;
import pl.openrest.generator.commons.RemoteClassNamingStrategy;
import pl.openrest.generator.commons.type.NonFinalOrStaticFieldFilter;
import pl.openrest.generator.commons.type.TypeFileWriter;
import pl.openrest.generator.commons.type.TypeResolver;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

@RunWith(MockitoJUnitRunner.class)
public class DtoTypeResolverTest {

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @Mock
    private RemoteClassNamingStrategy namingStrategy;

    @Mock
    private TypeFileWriter typeFileWriter;

    @Mock
    private TypeResolver typeResolver;

    @Mock
    private Configuration configuration;

    private FieldFilter fieldFilter = new NonFinalOrStaticFieldFilter();

    private DtoTypeResolver dtoTypeResolver;

    private String packageName = "pl.openrest.rdto.generator";
    private String remoteDtoClassName = "RTestDto";

    @Before
    public void setUp() {

        Mockito.when(configuration.get("defaultNamingStrategy")).thenReturn(namingStrategy);
        Mockito.when(configuration.get("typeFileWriter")).thenReturn(typeFileWriter);
        Mockito.when(configuration.get("defaultTypeResolver")).thenReturn(typeResolver);

        Mockito.when(typeResolver.supports(Mockito.any(Class.class))).thenReturn(true);

        Mockito.when(typeResolver.resolve(Mockito.any(Class.class))).then(new Answer<TypeName>() {

            @Override
            public TypeName answer(InvocationOnMock invocation) throws Throwable {
                return TypeName.get((Class<?>) invocation.getArguments()[0]);
            }
        });
        Mockito.when(namingStrategy.getClassName("TestDto")).thenReturn(remoteDtoClassName);
        Mockito.when(namingStrategy.getPackageName(Mockito.anyString())).thenReturn(packageName);

        dtoTypeResolver = new DtoTypeResolver(fieldFilter);
        dtoTypeResolver.setConfiguration(configuration);
    }

    @Test
    public void shouldResolveAppendBothClassAndSuperClassFields() throws Exception {
        // given
        // when
        dtoTypeResolver.resolve(TestDto.class);
        // then
        ArgumentCaptor<TypeSpec> dtoSpecCaptor = ArgumentCaptor.forClass(TypeSpec.class);
        Mockito.verify(typeFileWriter).write(dtoSpecCaptor.capture(), Mockito.eq(packageName));
        TypeSpec dtoSpec = dtoSpecCaptor.getValue();
        Assert.assertThat(
                dtoSpec.fieldSpecs.toString(),
                allOf(containsString("java.lang.String name"), containsString("java.lang.Long id"), containsString("boolean valid"),
                        containsString("boolean active")));
    }

    @Test
    public void shouldResolveAppendAnnotations() throws Exception {
        // given
        // when
        dtoTypeResolver.resolve(TestDto.class);
        // then
        ArgumentCaptor<TypeSpec> dtoSpecCaptor = ArgumentCaptor.forClass(TypeSpec.class);
        Mockito.verify(typeFileWriter).write(dtoSpecCaptor.capture(), Mockito.eq(packageName));
        TypeSpec dtoSpec = dtoSpecCaptor.getValue();
        Assert.assertThat(
                dtoSpec.annotations.toString(),
                allOf(containsString("@lombok.Getter"), containsString("@lombok.Setter"),
                        containsString("@javax.annotation.Generated(\"pl.openrest.rdto.generator\")")));
    }

    @Test
    public void shouldResolveAppendResolvedTypeByDefaultTypeResolver() throws Exception {
        // given
        Mockito.when(typeResolver.supports(String.class)).thenReturn(true);
        Mockito.when(typeResolver.resolve(String.class)).thenReturn(ClassName.get("java.util", "Date"));
        // when
        dtoTypeResolver.resolve(TestDto.class);
        // then
        ArgumentCaptor<TypeSpec> dtoSpecCaptor = ArgumentCaptor.forClass(TypeSpec.class);
        Mockito.verify(typeFileWriter).write(dtoSpecCaptor.capture(), Mockito.eq(packageName));
        TypeSpec dtoSpec = dtoSpecCaptor.getValue();
        Assert.assertThat(dtoSpec.fieldSpecs.toString(), containsString("java.util.Date name"));
    }

    @Test
    public void shouldResolveAppendStaticDtoNameField() throws Exception {
        // given
        dtoTypeResolver.resolve(TestDto.class);
        // then
        ArgumentCaptor<TypeSpec> dtoSpecCaptor = ArgumentCaptor.forClass(TypeSpec.class);
        Mockito.verify(typeFileWriter).write(dtoSpecCaptor.capture(), Mockito.eq(packageName));
        TypeSpec dtoSpec = dtoSpecCaptor.getValue();
        Assert.assertThat(dtoSpec.fieldSpecs.toString(),
                containsString("private static final java.lang.String DTO_NAME = \"testCreateDto\""));
    }

    @Test
    public void shouldResolveAppendDtoNameGetter() throws Exception {
        // given
        dtoTypeResolver.resolve(TestDto.class);
        // then
        ArgumentCaptor<TypeSpec> dtoSpecCaptor = ArgumentCaptor.forClass(TypeSpec.class);
        Mockito.verify(typeFileWriter).write(dtoSpecCaptor.capture(), Mockito.eq(packageName));
        TypeSpec dtoSpec = dtoSpecCaptor.getValue();
        Assert.assertThat(dtoSpec.methodSpecs.toString(),
                allOf(containsString("public java.lang.String getDtoName()"), containsString("return DTO_NAME;")));
    }

    @Test
    public void shouldResolveAppendPublicModifierToDtoType() throws Exception {
        // given
        dtoTypeResolver.resolve(TestDto.class);
        // then
        ArgumentCaptor<TypeSpec> dtoSpecCaptor = ArgumentCaptor.forClass(TypeSpec.class);
        Mockito.verify(typeFileWriter).write(dtoSpecCaptor.capture(), Mockito.eq(packageName));
        TypeSpec dtoSpec = dtoSpecCaptor.getValue();
        Assert.assertThat(dtoSpec.modifiers, hasItem(equalTo(Modifier.PUBLIC)));
    }

    @Dto(entityType = Object.class, type = DtoType.CREATE, name = "testCreateDto")
    private class TestDto extends TestDtoParent {
        private String name;
        private Long id;
        private boolean valid;
    }

    private class TestDtoParent {
        private boolean active;
    }

}
