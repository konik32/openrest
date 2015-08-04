package orest.annotation.utils;

import static org.junit.Assert.*;
import orest.dto.authorization.annotation.Secure;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class HierarchicalAnnotationUtilsTest {

	
	
	@Test
	public void shouldReturnListOfSize3() throws Exception {
		// given
		assertEquals(3, HierarchicalAnnotationUtils.getAllHierarchicalAnnotations(A.class, Secure.class).size());
		// when
		// then
	}
	
	
	
	@Secure("a")
	class A extends B{
		
	}
	
	@Secure("b")
	class B extends C{
		
	}
	
	@Secure("c")
	class C{
		
	}
}
