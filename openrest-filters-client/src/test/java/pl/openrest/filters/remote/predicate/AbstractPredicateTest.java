package pl.openrest.filters.remote.predicate;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.convert.ConversionService;

@RunWith(MockitoJUnitRunner.class)
public class AbstractPredicateTest {

    @Mock
    private ConversionService conversionService;
    private AbstractPredicate predicate;

    @Test
    public void shouldToPredicateReturnOnlyName() throws Exception {
        // given
        predicate = new AbstractPredicate("name") {
        };
        // when
        String result = predicate.toString(conversionService);
        // then
        Assert.assertEquals("name", result);
    }

    @Test
    public void shouldToPredicateReturnNameAndParametersJoinedWithComa() throws Exception {
        // given
        predicate = new AbstractPredicate("name", 1l, 2l) {
        };
        Mockito.when(conversionService.convert(Mockito.any(), Mockito.eq(String.class))).thenReturn("param");
        // when
        String result = predicate.toString(conversionService);
        // then
        Assert.assertEquals("name(param,param)", result);
    }

    @Test
    public void shouldToPredicateReturnStringWithEmptySpaceBetweenComasForNullParameter() throws Exception {
        // given
        predicate = new AbstractPredicate("name", 1l, null, 2l) {
        };
        Mockito.when(conversionService.convert(Mockito.any(), Mockito.eq(String.class))).thenReturn("param");
        // when
        String result = predicate.toString(conversionService);
        // then
        Assert.assertEquals("name(param,,param)", result);
    }

}
