package openrest.response.filter;

import javax.servlet.http.HttpServletRequest;

import openrest.response.filter.SpelFilter;
import openrest.response.filter.SpelMultiplePropertyFilter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import com.fasterxml.jackson.databind.ser.PropertyWriter;

@RunWith(MockitoJUnitRunner.class)
public class SpelMultiplePropertyFilterUnitTest {
	
	@Mock
	private HttpServletRequest request;
	
	@Mock
	private PropertyWriter writer;
	
	@InjectMocks
	private SpelMultiplePropertyFilter filter;
	
	
	@Test
	public void testPrepare(){
		filter.prepare(request,new TestObject("foo"));
		
		when(writer.getName()).thenReturn("name");
		
		assertTrue(filter.include(writer));
	}
	
	
	@SpelFilter(value="filteredObject.name=='foo'", properties="name")
	public static class TestObject{
		public String name;

		public TestObject(String name) {
			super();
			this.name = name;
		}
	}
}
