package pl.openrest.filters.predicate;

import static org.junit.Assert.assertArrayEquals;

import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.util.ReflectionUtils;

@RunWith(MockitoJUnitRunner.class)
public class ConversionServiceBasedMethodParameterConverterTest {

    @Mock
    private ConversionService conversionService;

    private ConversionServiceBasedMethodParameterConverter converter;

    @Before
    public void setUp() {
        converter = new ConversionServiceBasedMethodParameterConverter(conversionService);
    }

    @Test
    public void shouldCallConversionServiceTwice() throws Exception {
        // given
        String rawParameters[] = { "2", "false" };
        Method method = ReflectionUtils.findMethod(TestClass.class, "testMethod", Long.class, Boolean.class);
        // when
        Object[] result = converter.convert(method, rawParameters);
        // then
        Mockito.verify(conversionService, Mockito.times(2)).convert(Mockito.any(), Mockito.any(TypeDescriptor.class),
                Mockito.any(TypeDescriptor.class));
    }

    private static class TestClass {
        public void testMethod(Long id, Boolean valid) {

        }
    }
}
