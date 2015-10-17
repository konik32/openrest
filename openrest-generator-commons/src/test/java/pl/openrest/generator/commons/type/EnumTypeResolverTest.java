package pl.openrest.generator.commons.type;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import pl.openrest.generator.commons.Configuration;
import pl.openrest.generator.commons.RemoteClassNamingStrategy;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeSpec;

@RunWith(MockitoJUnitRunner.class)
public class EnumTypeResolverTest {
    @Mock
    private TypeFileWriter typeFileWriter;

    @Mock
    private RemoteClassNamingStrategy namingStrategy;

    @Mock
    private Configuration configuration;

    private EnumTypeResolver enumTypeResolver;

    private String remoteClassName = "RTestClassEnum";
    private String packageName = "pl.openrest.generator.commons.type";

    @Before
    public void setUp() {
        enumTypeResolver = new EnumTypeResolver();
        Mockito.when(configuration.get("defaultNamingStrategy")).thenReturn(namingStrategy);
        Mockito.when(configuration.get("typeFileWriter")).thenReturn(typeFileWriter);
        
        enumTypeResolver.setConfiguration(configuration);
        Mockito.when(namingStrategy.getClassName("TestEnum")).thenReturn("RTestClassEnum");
        Mockito.when(namingStrategy.getPackageName(packageName)).thenReturn(packageName);
    }

    @Test
    public void shouldResolveEnumWithNameAndPackageFromNamingStrategy() throws Exception {
        // given
        // when
        ClassName remoteEnum = enumTypeResolver.resolve(TestEnum.class);
        // then
        assertEquals(remoteClassName, remoteEnum.simpleName());
        assertEquals(packageName, remoteEnum.packageName());

    }

    @Test
    public void shouldResolveEnumCallTypeFileWriterWithTypeSpecEnumWithConstants() throws Exception {
        // given
        // when
        enumTypeResolver.resolve(TestEnum.class);
        // then
        ArgumentCaptor<TypeSpec> argumentCaptor = ArgumentCaptor.forClass(TypeSpec.class);
        Mockito.verify(typeFileWriter).write(argumentCaptor.capture(), Mockito.eq(packageName));
        assertThat(argumentCaptor.getValue().enumConstants, Matchers.allOf(Matchers.hasKey("FIRST"), Matchers.hasKey("SECOND")));
    }

    public enum TestEnum {
        FIRST, SECOND;
    }

}
