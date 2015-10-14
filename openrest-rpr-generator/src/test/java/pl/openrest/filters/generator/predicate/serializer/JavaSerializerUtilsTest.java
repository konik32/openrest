package pl.openrest.filters.generator.predicate.serializer;

import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.util.Arrays;

import javax.annotation.Generated;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import pl.openrest.filters.generator.predicate.context.PredicateInformationFactory.ParameterInformation;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.ParameterSpec;

public class JavaSerializerUtilsTest {

    @Test
    public void shouldReturnEmptyStringOnEmptyParameterArray() throws Exception {
        // given
        // when
        ParameterSpec parameterSpecs[] = JavaSerializerUtils.createParameters(new ParameterInformation[0]);
        // then
        Assert.assertArrayEquals(new ParameterSpec[0], parameterSpecs);
    }

    @Test
    public void shouldCreateParamterSpecArrayFromParamterArray() throws Exception {
        // given
        ParameterInformation pInfos[] = new ParameterInformation[] { new ParameterInformation("date", LocalDateTime.class),
                new ParameterInformation("object", Object.class) };
        // when
        ParameterSpec parameterSpecs[] = JavaSerializerUtils.createParameters(pInfos);
        // then
        Assert.assertEquals("java.time.LocalDateTime date", parameterSpecs[0].toString());
        Assert.assertEquals("java.lang.Object object", parameterSpecs[1].toString());
    }

    @Test
    public void shouldCreateMethodNameStaticFieldWithUpperUnderscoredName() throws Exception {
        // given
        String name = "userIdEq";
        // when
        FieldSpec spec = JavaSerializerUtils.createMethodNameStaticField(name);
        // then
        Assert.assertEquals("USER_ID_EQ", spec.name);
        Assert.assertEquals("\"" + name + "\"", spec.initializer.toString());
    }

    @Test
    public void shouldCreateDefaultedPageableFieldSpec() throws Exception {
        // given
        String name = "DEFAULTED_PAGEABLE";
        // when
        FieldSpec spec = JavaSerializerUtils.createDefaultedPageableField(false);
        // then
        Assert.assertEquals(name, spec.name);
        Assert.assertEquals(boolean.class.getSimpleName(), spec.type.toString());
        Assert.assertEquals("false", spec.initializer.toString());
    }

    @Test
    public void shouldCreateGeneratedAnnotationSpec() throws Exception {
        // given
        // when
        AnnotationSpec spec = JavaSerializerUtils.createGeneratedAnnotationSpec();
        // then
        Assert.assertEquals(Generated.class.getName(), spec.type.toString());
        Assert.assertEquals("@javax.annotation.Generated(\"pl.openrest.rpr.generator\")", spec.toString());
    }

}