package pl.openrest.rpr.generator;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.reflections.Reflections;

import pl.openrest.filters.predicate.annotation.Predicate;
import pl.openrest.filters.predicate.annotation.Predicate.PredicateType;
import pl.openrest.filters.predicate.annotation.PredicateRepository;
import pl.openrest.generator.commons.Configuration;
import pl.openrest.generator.commons.DefaultRemoteClassNamingStrategy;
import pl.openrest.generator.commons.RemoteClassNamingStrategy;
import pl.openrest.generator.commons.type.TypeFileWriter;
import pl.openrest.generator.commons.type.TypeResolver;
import pl.openrest.rpr.generator.PredicateRepositoryResolver;
import pl.openrest.rpr.generator.test.repository.UserPredicateRepository;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeSpec;

@RunWith(MockitoJUnitRunner.class)
public class PredicateRepositoryResolverTest {

    private RemoteClassNamingStrategy namingStrategy = new DefaultRemoteClassNamingStrategy();

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @Mock
    private TypeFileWriter typeFileWriter;

    @Mock
    private TypeResolver typeResolver;

    @Mock
    private Configuration configuration;

    @Mock
    private Reflections reflections;

    private PredicateRepositoryResolver resolver;
    private File outputDirectory;

    @Before
    public void setUp() throws Exception {

        Mockito.when(configuration.get("defaultNamingStrategy")).thenReturn(namingStrategy);
        Mockito.when(configuration.get("typeFileWriter")).thenReturn(typeFileWriter);
        Mockito.when(configuration.get("defaultTypeResolver")).thenReturn(typeResolver);
        Mockito.when(configuration.get("reflections")).thenReturn(reflections);

        Mockito.when(reflections.getMethodParamNames(Mockito.refEq(UserPredicateRepository.class.getMethod("nameEq", String.class))))
                .thenReturn(Arrays.asList("name"));
        Mockito.when(reflections.getMethodParamNames(Mockito.refEq(UserPredicateRepository.class.getMethod("active")))).thenReturn(
                Collections.<String> emptyList());
        Mockito.when(reflections.getMethodParamNames(Mockito.refEq(UserPredicateRepository.class.getMethod("sort", String.class))))
                .thenReturn(Arrays.asList("address"));

        Mockito.when(typeResolver.supports(String.class)).thenReturn(true);
        Mockito.when(typeResolver.resolve(String.class)).thenReturn(ClassName.get(String.class));

        outputDirectory = testFolder.newFolder();
        resolver = new PredicateRepositoryResolver();
        resolver.setConfiguration(configuration);
    }

    @Test
    public void shouldResolveCallTypeResolverForParameters() throws Exception {
        // given
        // when
        resolver.resolve(UserPredicateRepository.class);
        // then
        Mockito.verify(typeResolver, Mockito.times(2)).resolve(String.class);
    }

    @Test
    public void shouldResolveAppendAnnotations() throws Exception {
        // given
        // when
        resolver.resolve(UserPredicateRepository.class);
        // then
        ArgumentCaptor<TypeSpec> dtoSpecCaptor = ArgumentCaptor.forClass(TypeSpec.class);
        Mockito.verify(typeFileWriter).write(dtoSpecCaptor.capture(), Mockito.anyString());
        TypeSpec spec = dtoSpecCaptor.getValue();
        Assert.assertThat(spec.annotations.toString(), containsString("@javax.annotation.Generated(\"pl.openrest.rpr.generator\")"));
    }

    @Test
    public void shouldResolveAppendStaticDefaultedPageableField() throws Exception {
        // given
        resolver.resolve(UserPredicateRepository.class);
        // then
        ArgumentCaptor<TypeSpec> dtoSpecCaptor = ArgumentCaptor.forClass(TypeSpec.class);
        Mockito.verify(typeFileWriter).write(dtoSpecCaptor.capture(), Mockito.anyString());
        TypeSpec spec = dtoSpecCaptor.getValue();
        Assert.assertThat(spec.fieldSpecs.toString(), containsString("private static final boolean DEFAULTED_PAGEABLE = false"));
    }

    @Test
    public void shouldResolveAppendStaticMethodNamesFields() throws Exception {
        // given
        resolver.resolve(UserPredicateRepository.class);
        // then
        ArgumentCaptor<TypeSpec> dtoSpecCaptor = ArgumentCaptor.forClass(TypeSpec.class);
        Mockito.verify(typeFileWriter).write(dtoSpecCaptor.capture(), Mockito.anyString());
        TypeSpec spec = dtoSpecCaptor.getValue();
        Assert.assertThat(
                spec.fieldSpecs.toString(),
                allOf(containsString("private static final java.lang.String NAME_EQ = \"nameEq\""),
                        containsString("private static final java.lang.String ACTIVE = \"active\""),
                        containsString("private static final java.lang.String SORT = \"sort\"")));
    }

    @Test
    public void shouldResolveAppendStaticPredicateMethods() throws Exception {
        // given
        resolver.resolve(UserPredicateRepository.class);
        // then
        ArgumentCaptor<TypeSpec> dtoSpecCaptor = ArgumentCaptor.forClass(TypeSpec.class);
        Mockito.verify(typeFileWriter).write(dtoSpecCaptor.capture(), Mockito.anyString());
        TypeSpec spec = dtoSpecCaptor.getValue();
        Assert.assertThat(
                spec.methodSpecs.toString(),
                allOf(containsString("public static pl.openrest.filters.remote.predicate.SearchPredicate nameEq(java.lang.String name)"),
                        containsString("return new pl.openrest.filters.remote.predicate.SearchPredicate(NAME_EQ,true,name)"),
                        containsString("public static pl.openrest.filters.remote.predicate.FilterPredicate active()"),
                        containsString("return new pl.openrest.filters.remote.predicate.FilterPredicate(ACTIVE)"),
                        containsString("public static pl.openrest.filters.remote.predicate.SortPredicate sort(java.lang.String address)"),
                        containsString("return new pl.openrest.filters.remote.predicate.SortPredicate(SORT,address)")));
    }

}
