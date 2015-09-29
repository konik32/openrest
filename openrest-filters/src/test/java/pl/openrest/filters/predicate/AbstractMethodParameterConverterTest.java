package pl.openrest.filters.predicate;

import static org.junit.Assert.assertArrayEquals;

import java.lang.reflect.Method;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.MethodParameter;
import org.springframework.util.ReflectionUtils;

@RunWith(MockitoJUnitRunner.class)
public class AbstractMethodParameterConverterTest {

    private static final Object EMPTY_ARRAY[] = new Object[0];

    private AbstractMethodParameterConverter converter;

    @Before
    public void setUp() {
        converter = Mockito.mock(AbstractMethodParameterConverter.class, Mockito.CALLS_REAL_METHODS);

    }

    @Test
    public void shouldReturnEmptyArrayObjectOnNullRawParametersAndMethodWithoutParameters() throws Exception {
        // given
        String rawParameters[] = null;
        Method method = ReflectionUtils.findMethod(TestClass.class, "testMethodWithoutParams");
        // when
        // then
        Assert.assertArrayEquals(EMPTY_ARRAY, converter.convert(method, rawParameters));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionOnRawParametersCountNullAndMethodHasParameters() throws Exception {
        // given
        String rawParameters[] = null;
        Method method = ReflectionUtils.findMethod(TestClass.class, "testMethod", Long.class, Boolean.class);
        // when
        converter.convert(method, rawParameters);
        // then
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionOnRawParametersCountDoesNotMatchMethodParametersCount() throws Exception {
        // given
        String rawParameters[] = { "2" };
        Method method = ReflectionUtils.findMethod(TestClass.class, "testMethod", Long.class, Boolean.class);
        // when
        converter.convert(method, rawParameters);
        // then
    }

    @Test
    public void shouldReturnConverterParameters() throws Exception {
        // given
        String rawParameters[] = { "2", "false" };
        Method method = ReflectionUtils.findMethod(TestClass.class, "testMethod", Long.class, Boolean.class);
        Mockito.when(converter.doConvert(Mockito.any(MethodParameter.class), Mockito.eq("2"))).thenReturn(2l);
        Mockito.when(converter.doConvert(Mockito.any(MethodParameter.class), Mockito.eq("false"))).thenReturn(false);
        // when
        Object[] result = converter.convert(method, rawParameters);
        // then
        assertArrayEquals(new Object[] { 2l, false }, result);
    }

    private static class TestClass {
        public void testMethod(Long id, Boolean valid) {

        }

        public void testMethodWithoutParams() {

        }
    }
}
