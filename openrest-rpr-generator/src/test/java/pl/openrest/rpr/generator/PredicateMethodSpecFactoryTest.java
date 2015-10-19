package pl.openrest.rpr.generator;

import java.time.LocalDateTime;

import javax.lang.model.element.Modifier;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import pl.openrest.filters.remote.predicate.FilterPredicate;
import pl.openrest.filters.remote.predicate.SearchPredicate;
import pl.openrest.generator.commons.type.TypeResolver;
import pl.openrest.rpr.generator.PredicateMethodInformation;
import pl.openrest.rpr.generator.PredicateMethodSpecFactory;
import pl.openrest.rpr.generator.PredicateMethodInformation.ParameterInformation;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;

@RunWith(MockitoJUnitRunner.class)
public class PredicateMethodSpecFactoryTest {

    @Mock
    private TypeResolver typeResolver;

    private PredicateMethodSpecFactory factory;

    @Before
    public void setUp() {
        Mockito.when(typeResolver.resolve(Long.class)).thenReturn(ClassName.get(Long.class));
        Mockito.when(typeResolver.resolve(LocalDateTime.class)).thenReturn(ClassName.get(Long.class));

        factory = new PredicateMethodSpecFactory(typeResolver);
    }

    @Test
    public void shouldCreateMethodSpecWithFilterPredicateReturnStatement() throws Exception {
        // given
        PredicateMethodInformation predicateMethodInformation = new PredicateMethodInformation("userIdEq", false, FilterPredicate.class,
                new ParameterInformation("id", Long.class), new ParameterInformation("date", LocalDateTime.class));

        // when
        MethodSpec spec = factory.create(predicateMethodInformation);
        // then
        Assert.assertEquals(predicateMethodInformation.getName(), spec.name);
        Assert.assertThat(spec.modifiers, Matchers.contains(Modifier.PUBLIC, Modifier.STATIC));
        Assert.assertThat(spec.parameters, Matchers.hasSize(2));
        Assert.assertEquals(FilterPredicate.class.getName(), spec.returnType.toString());
        Assert.assertEquals(
                String.format("return new pl.openrest.filters.remote.predicate.FilterPredicate(%s,%s,%s);\n",
                        predicateMethodInformation.getUpperCaseName(), "id", "date"), spec.code.toString());
    }

    @Test
    public void shouldCreateMethodSpecWithSearchPredicateReturnStatement() throws Exception {
        // given
        PredicateMethodInformation predicateMethodInformation = new PredicateMethodInformation("userIdEq", false, SearchPredicate.class,
                new ParameterInformation("id", Long.class), new ParameterInformation("date", LocalDateTime.class));
        // when
        MethodSpec spec = factory.create(predicateMethodInformation);
        // then
        Assert.assertEquals(predicateMethodInformation.getName(), spec.name);
        Assert.assertThat(spec.modifiers, Matchers.contains(Modifier.PUBLIC, Modifier.STATIC));
        Assert.assertThat(spec.parameters, Matchers.hasSize(2));
        Assert.assertEquals(SearchPredicate.class.getName(), spec.returnType.toString());
        Assert.assertEquals(
                String.format("return new pl.openrest.filters.remote.predicate.SearchPredicate(%s,%s,%s,%s);\n",
                        predicateMethodInformation.getUpperCaseName(), predicateMethodInformation.isDefaultedPageable(), "id", "date"),
                spec.code.toString());
    }

    @Test
    public void shouldCreateMethodSpecWithSearchPredicateWithoutParameters() throws Exception {
        // given
        PredicateMethodInformation predicateMethodInformation = new PredicateMethodInformation("userIdEq", false, SearchPredicate.class);
        // when
        MethodSpec spec = factory.create(predicateMethodInformation);
        // then
        Assert.assertEquals(
                String.format("return new pl.openrest.filters.remote.predicate.SearchPredicate(%s,%s);\n",
                        predicateMethodInformation.getUpperCaseName(), predicateMethodInformation.isDefaultedPageable()),
                spec.code.toString());
    }

    @Test
    public void shouldCreateMethodSpecWithFilterPredicateWithoutParameters() throws Exception {
        // given
        PredicateMethodInformation predicateMethodInformation = new PredicateMethodInformation("userIdEq", false, FilterPredicate.class);
        // when
        MethodSpec spec = factory.create(predicateMethodInformation);
        // then
        Assert.assertEquals(
                String.format("return new pl.openrest.filters.remote.predicate.FilterPredicate(%s);\n",
                        predicateMethodInformation.getUpperCaseName(), predicateMethodInformation.isDefaultedPageable()),
                spec.code.toString());
    }
}
