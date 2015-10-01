package pl.openrest.filters.query;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.util.ReflectionUtils;

import pl.openrest.filters.domain.registry.FilterableEntityInformation;
import pl.openrest.filters.predicate.IdConverter;
import pl.openrest.filters.predicate.MethodParameterConverter;
import pl.openrest.filters.predicate.StaticFilterConditionEvaluator;
import pl.openrest.filters.predicate.registry.PredicateInformation;
import pl.openrest.filters.query.PredicateContextBuilderFactory.PredicateContextBuilder;
import pl.openrest.filters.query.registry.JoinInformation;
import pl.openrest.filters.query.registry.StaticFilterInformation;
import pl.openrest.predicate.parser.DefaultFilterTreeBuilder;
import pl.openrest.predicate.parser.DefaultPredicatePartsExtractor;
import pl.openrest.predicate.parser.FilterPart;
import pl.openrest.predicate.parser.FilterTreeBuilder;
import pl.openrest.predicate.parser.PredicateParts;

import com.mysema.query.types.expr.BooleanExpression;

@RunWith(MockitoJUnitRunner.class)
public class PredicateContextBuilderTest {

    @Mock
    private IdConverter idConverter;

    @Mock
    private MethodParameterConverter predicateParameterConverter;

    @Mock
    private MethodParameterConverter staticFilterParametersConverter;

    @Mock
    private StaticFilterConditionEvaluator staticFilterConditionEvaluator;

    @Mock
    private FilterableEntityInformation entityInfo;

    @Mock
    private StaticFilterInformation staticFilterInformation;

    @Mock
    private PredicateInformation predicateInformation;

    @Mock
    private JoinInformation joinInformation;

    private Method method = ReflectionUtils.findMethod(TestClass.class, "testMethod");

    private PredicateContextBuilderFactory builderFactory;

    @Before
    public void setUp() {
        Mockito.when(entityInfo.getStaticFilters()).thenReturn(Arrays.asList(staticFilterInformation, staticFilterInformation));
        Mockito.doReturn(Object.class).when(entityInfo).getEntityType();
        Mockito.when(entityInfo.getPredicateRepository()).thenReturn(new TestClass());
        Mockito.when(staticFilterInformation.getPredicateInformation()).thenReturn(predicateInformation);
        Mockito.when(predicateInformation.getMethod()).thenReturn(method);
        Mockito.when(predicateInformation.getJoins()).thenReturn(Arrays.asList(joinInformation, joinInformation));
        Mockito.when(entityInfo.getPredicateInformation(Mockito.anyString())).thenReturn(predicateInformation);

        builderFactory = new PredicateContextBuilderFactory(predicateParameterConverter, staticFilterParametersConverter, idConverter);
    }

    @Test
    public void shouldCallEntityInfoGetStaticFilters() throws Exception {
        // given
        PredicateContextBuilder builder = builderFactory.create(entityInfo);
        // when
        builder.withStaticFilters();
        // then
        Mockito.verify(entityInfo, Mockito.times(1)).getStaticFilters();
    }

    @Test
    public void shouldCallStaticFilterParametersConverter() throws Exception {
        // given
        PredicateContextBuilder builder = builderFactory.create(entityInfo);
        String parameters[] = new String[] { "1", "2" };
        Mockito.when(staticFilterInformation.getParameters()).thenReturn(parameters);
        // when
        builder.withStaticFilters();
        // then
        Mockito.verify(staticFilterParametersConverter, Mockito.times(2)).convert(method, parameters);
    }

    @Test
    public void shouldNotAppendStaticFilterWhenStaticFilterConditionEvaluatorReturnsTrue() throws Exception {
        // given
        StaticFilterInformation condintionalStaticFilter = Mockito.mock(StaticFilterInformation.class);
        Mockito.when(condintionalStaticFilter.getCondition()).thenReturn("1==1");
        Mockito.when(staticFilterConditionEvaluator.evaluateCondition(condintionalStaticFilter.getCondition())).thenReturn(true);
        Mockito.when(staticFilterConditionEvaluator.evaluateCondition(staticFilterInformation.getCondition())).thenReturn(false);
        Mockito.when(entityInfo.getStaticFilters()).thenReturn(Arrays.asList(condintionalStaticFilter, staticFilterInformation));
        builderFactory.setStaticFilterConditionEvaluator(staticFilterConditionEvaluator);
        PredicateContextBuilder builder = builderFactory.create(entityInfo);
        // when
        builder.withStaticFilters();
        // then
        Mockito.verify(staticFilterConditionEvaluator, Mockito.times(2)).evaluateCondition(Mockito.anyString());
        Mockito.verify(staticFilterParametersConverter, Mockito.times(1)).convert(Mockito.any(Method.class), Mockito.any(String[].class));
    }

    @Test
    public void shouldCallIdConverter() throws Exception {
        // given
        PredicateContextBuilder builder = builderFactory.create(entityInfo);
        Mockito.doReturn(TestClass.class).when(entityInfo).getEntityType();
        PersistentProperty idProperty = Mockito.mock(PersistentProperty.class);
        Mockito.when(idProperty.getName()).thenReturn("id");
        String idValue = "2";
        Mockito.when(idConverter.convert(idProperty, idValue)).thenReturn(2l);
        // when
        builder.withId(idProperty, idValue);
        // then
        Mockito.verify(idConverter, Mockito.times(1)).convert(idProperty, idValue);
    }

    @Test
    public void shouldCallPredicateParametersConverter() throws Exception {
        // given
        PredicateContextBuilder builder = builderFactory.create(entityInfo);
        String parameters[] = new String[] { "1", "2" };
        PredicateParts predicateParts = new PredicateParts("testMethod", parameters);
        // when
        builder.withPredicateParts(predicateParts);
        // then
        Mockito.verify(predicateParameterConverter, Mockito.times(1)).convert(method, parameters);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionWhenPredicateInformationNotFound() throws Exception {
        // given
        String parameters[] = new String[] { "1", "2" };
        PredicateParts predicateParts = new PredicateParts("testMethod", parameters);
        Mockito.when(entityInfo.getPredicateInformation(predicateParts.getPredicateName())).thenReturn(null);
        PredicateContextBuilder builder = builderFactory.create(entityInfo);
        // when
        builder.withPredicateParts(predicateParts);
        // then
    }

    @Test
    public void shouldAppendJoinsFromPredicateParts() throws Exception {
        // given
        PredicateParts predicateParts = new PredicateParts("testMethod", null);
        PredicateContextBuilder builder = builderFactory.create(entityInfo);
        // when
        builder.withPredicateParts(predicateParts);
        PredicateContext context = builder.build();
        // then
        Assert.assertEquals(Arrays.asList(joinInformation, joinInformation), context.getJoins());
    }

    @Test
    public void shouldAppendJoinsFromStaticFilters() throws Exception {
        // given
        PredicateContextBuilder builder = builderFactory.create(entityInfo);
        // when
        builder.withStaticFilters();
        PredicateContext context = builder.build();
        // then
        Assert.assertEquals(Arrays.asList(joinInformation, joinInformation, joinInformation, joinInformation), context.getJoins());
    }

    @Test
    public void shouldVisitEveryFilterPartInTreeInCorrectOrder() throws Exception {
        // given
        FilterTreeBuilder filterTreeBuilder = new DefaultFilterTreeBuilder(new DefaultPredicatePartsExtractor());
        FilterPart tree = filterTreeBuilder.from("userIdEq(1;3);or;yearBetween(3;4);and;priceBetween(3;5)");
        PredicateContextBuilder builder = builderFactory.create(entityInfo);
        // when
        builder.withFilterTree(tree);
        // then
        InOrder inOrder = Mockito.inOrder(entityInfo);
        inOrder.verify(entityInfo).getPredicateInformation("userIdEq");
        inOrder.verify(entityInfo).getPredicateInformation("yearBetween");
        inOrder.verify(entityInfo).getPredicateInformation("priceBetween");
    }

    public static class TestClass {

        private Long id;

        public BooleanExpression testMethod() {
            return null;
        }
    }

}