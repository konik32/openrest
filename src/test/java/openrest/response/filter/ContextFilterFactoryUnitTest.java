package openrest.response.filter;

import javax.servlet.http.HttpServletRequest;

import openrest.response.filter.AbstractContextFilter;
import openrest.response.filter.ContextFilterFactory;

import org.junit.Test;

import static org.junit.Assert.*;


public class ContextFilterFactoryUnitTest {

	@Test
	public void doesGetDifferentFilterInstancesForDifferentObjects() throws Exception{
		ContextFilterFactory factory = new ContextFilterFactory();
		AbstractContextFilter filter1 = factory.get(new Object(), TestFilter.class);
		AbstractContextFilter filter2 = factory.get(new Object(), TestFilter.class);
		
		assertNotEquals(filter1, filter2);
	}
	
	public static class TestFilter extends AbstractContextFilter{

		@Override
		public void prepare(HttpServletRequest request, Object valueToFilter) {
			// TODO Auto-generated method stub
			
		}
		
	}
}
