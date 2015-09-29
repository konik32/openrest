package pl.openrest.filters.predicate;

import static org.junit.Assert.assertArrayEquals;

import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.util.ReflectionUtils;

@RunWith(MockitoJUnitRunner.class)
public class SpelMethodParameterConverterTest {

    @Mock
    private BeanFactory beanFactory;

    private SpelMethodParameterConverter converter;

    @Before
    public void setUp() {
        converter = new SpelMethodParameterConverter(beanFactory);
    }

    @Test
    public void shouldConvertSpelExpressionsToMethoParameters() throws Exception {
        // given
        String rawParameters[] = new String[] { "1+1", "2!=2" };
        Method method = ReflectionUtils.findMethod(TestClass.class, "testMethod", Long.class, Boolean.class);
        // when
        Object[] result = converter.convert(method, rawParameters);
        // then
        assertArrayEquals(new Object[] { 2l, false }, result);
    }

    private static class TestClass {
        public void testMethod(Long id, Boolean valid) {

        }
    }
}
