package pl.openrest.generator.commons.type;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import pl.openrest.generator.commons.Configuration;

import com.squareup.javapoet.ClassName;

@RunWith(MockitoJUnitRunner.class)
public class TypeResolverCompositeTest {

    @Mock
    private Configuration configuration;

    @Mock
    private TypeResolver typeResolver;

    private TypeResolverComposite composite;

    @Before
    public void setUp() {
        composite = new TypeResolverComposite(Arrays.asList(typeResolver));
        composite.setConfiguration(configuration);
    }

    @Test
    public void shouldResolveCallTypeResolverOnlyOnce() throws Exception {
        // given
        ClassName className = ClassName.get(TypeResolverCompositeTest.class);
        Mockito.when(typeResolver.supports(TypeResolverCompositeTest.class)).thenReturn(true);
        Mockito.when(typeResolver.resolve(TypeResolverCompositeTest.class)).thenReturn(className);
        // when
        composite.resolve(TypeResolverCompositeTest.class);
        composite.resolve(TypeResolverCompositeTest.class);
        // then
        Mockito.verify(typeResolver, Mockito.times(1)).resolve(TypeResolverCompositeTest.class);
    }

    @Test
    public void shouldSupportsReturnTrueOnTypeResolverSupportType() throws Exception {
        // given
        Mockito.when(typeResolver.supports(TypeResolverCompositeTest.class)).thenReturn(true);
        // when
        boolean supports = composite.supports(TypeResolverCompositeTest.class);
        // then
        assertTrue(supports);
    }

    @Test
    public void shouldSupportsReturnFalseOnNoTypeResolverSupports() throws Exception {
        // given
        Mockito.when(typeResolver.supports(TypeResolverCompositeTest.class)).thenReturn(false);
        // when
        boolean supports = composite.supports(TypeResolverCompositeTest.class);
        // then
        assertFalse(supports);
    }

}
