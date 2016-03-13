package pl.openrest.dto.mappers.generator;

import lombok.Getter;
import lombok.Setter;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.util.ReflectionUtils;

import pl.openrest.dto.annotations.Nullable;

public class MappedFieldInformationTest {

	private MappedFieldInformation dtoFieldInfo;
	
	
    @Test
    public void shouldIsNullableReturnTrueOnNullableAnnotationPresent() throws Exception {
        // given
    	dtoFieldInfo = new MappedFieldInformation(ReflectionUtils.findField(TestClass.class, "street"),false);
        // when
        // then
    	Assert.assertTrue(dtoFieldInfo.isNullable());
    }
    
    
    @Test(expected=IllegalStateException.class)
    public void shouldGetNullableGetterNameThrowExceptionOnNoGetterMethodOfNullableField() throws Exception {
        // given
    	dtoFieldInfo = new MappedFieldInformation(ReflectionUtils.findField(TestClass.class, "city"),false);
        // when
    	dtoFieldInfo.getNullableGetterName();
        // then
    }
    
    @Test
    public void shouldGetNullableGetterNameReturnName() throws Exception {
        // given
    	dtoFieldInfo = new MappedFieldInformation(ReflectionUtils.findField(TestClass.class, "street"),false);
        // when
    	String result = dtoFieldInfo.getNullableGetterName();
        // then
    	Assert.assertEquals("isStreetSet", result);
    }
    
    @Test
    public void shouldIsPrimitiveReturnTrueOnPrimitiveType() throws Exception {
        // given
    	dtoFieldInfo = new MappedFieldInformation(ReflectionUtils.findField(TestClass.class, "employed"),false);
        // when
    	boolean result = dtoFieldInfo.isPrimitive();
        // then
    	Assert.assertTrue(result);
    }
    
    
    public static class TestClass{
    	
    	private @Getter @Setter boolean streetSet;
    	private @Setter boolean citySet;
    	
    	@Nullable("streetSet")
    	private @Setter @Getter String street;
    	
    	@Nullable("citySet")
    	private @Setter @Getter String city;
    	
    	private @Setter @Getter boolean employed;
    }
}
