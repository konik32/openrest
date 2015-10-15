package pl.openrest.filters.generator.predicate.context;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.reflections.Reflections;

import pl.openrest.filters.generator.predicate.context.PredicateInformationFactory.ParameterInformation;
import pl.openrest.filters.predicate.annotation.Predicate;
import pl.openrest.filters.predicate.annotation.Predicate.PredicateType;
import pl.openrest.filters.remote.predicate.AbstractPredicate;
import pl.openrest.filters.remote.predicate.FilterPredicate;
import pl.openrest.filters.remote.predicate.SearchPredicate;
import pl.openrest.filters.remote.predicate.SortPredicate;

@RunWith(MockitoJUnitRunner.class)
public class PredicateInformationFactoryTest {

    @Mock
    private Reflections reflections;

    private PredicateInformationFactory predicateInformationFactory;

    @Before
    public void setUp() {
        predicateInformationFactory = new PredicateInformationFactory(reflections);
        Mockito.when(reflections.getMethodParamNames(Mockito.any(Method.class))).thenReturn(Arrays.asList("id", "date"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionOnMethodNotAnnotatedWithPredicate() throws Exception {
        // given
        // when
        predicateInformationFactory.from(PredicateInformationFactoryTest.class.getMethod("testMethod", Long.class, LocalDateTime.class));
        // then
    }

    @Test
    public void shouldCreateSearchPredicateInformationFromMethod() throws Exception {
        // given
        final Method method = PredicateInformationFactoryTest.class.getMethod("testMethodWithAnnotation", Long.class, LocalDateTime.class);
        // when
        AbstractPredicate predicate = predicateInformationFactory.from(method);
        // then
        Assert.assertEquals(method.getName(), predicate.getName());
        Assert.assertEquals(new ParameterInformation("id", Long.class), predicate.getParameters()[0]);
        Assert.assertEquals(new ParameterInformation("date", LocalDateTime.class), predicate.getParameters()[1]);
        Assert.assertEquals(SearchPredicate.class, predicate.getClass());
        Assert.assertEquals(false, ((SearchPredicate) predicate).isDefaultedPageable());
    }

    @Test
    public void shouldCreateFilterPredicateInformationFromMethod() throws Exception {
        // given
        final Method method = PredicateInformationFactoryTest.class.getMethod("testFilterPredicate", Long.class, LocalDateTime.class);
        // when
        AbstractPredicate predicate = predicateInformationFactory.from(method);
        // then
        Assert.assertEquals(method.getName(), predicate.getName());
        Assert.assertEquals(new ParameterInformation("id", Long.class), predicate.getParameters()[0]);
        Assert.assertEquals(new ParameterInformation("date", LocalDateTime.class), predicate.getParameters()[1]);
        Assert.assertEquals(FilterPredicate.class, predicate.getClass());
    }

    @Test
    public void shouldCreateSortPredicateInformationFromMethod() throws Exception {
        // given
        final Method method = PredicateInformationFactoryTest.class.getMethod("testSortPredicate", Long.class, LocalDateTime.class);
        // when
        AbstractPredicate predicate = predicateInformationFactory.from(method);
        // then
        Assert.assertEquals(method.getName(), predicate.getName());
        Assert.assertEquals(new ParameterInformation("id", Long.class), predicate.getParameters()[0]);
        Assert.assertEquals(new ParameterInformation("date", LocalDateTime.class), predicate.getParameters()[1]);
        Assert.assertEquals(SortPredicate.class, predicate.getClass());
    }

    public void testMethod(Long id, LocalDateTime date) {

    }

    @Predicate(defaultedPageable = false, type = PredicateType.SEARCH)
    public void testMethodWithAnnotation(Long id, LocalDateTime date) {

    }

    @Predicate(defaultedPageable = false, type = PredicateType.FILTER)
    public void testFilterPredicate(Long id, LocalDateTime date) {

    }

    @Predicate(defaultedPageable = false, type = PredicateType.SORT)
    public void testSortPredicate(Long id, LocalDateTime date) {

    }

    private static class Element {
        private int a;
        public Element next;

        public Element(int a, Element next) {
            this.a = a;
            this.next = next;
        }
    }

}
