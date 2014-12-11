package openrest.response.filter;

import openrest.response.filter.ContextFilterFactory;
import openrest.response.filter.ContextFilterProvider;
import openrest.response.filter.SpelMultiplePropertyFilter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

import com.fasterxml.jackson.databind.ser.PropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;

import static org.mockito.Mockito.*;
@RunWith(MockitoJUnitRunner.class)
public class BoostFilterProviderUnitTest {

	private static String FILTER_ID = "spelFilter";
	private static String OTHER_FILTER_ID = "other";
	
	@Mock ContextFilterFactory contextFilterFactory;
	@Mock SimpleBeanPropertyFilter simpleFilter;
	
	@InjectMocks
	private ContextFilterProvider provider;
	
	@Before
	public void setUp(){
		provider.addContextFilter(FILTER_ID, SpelMultiplePropertyFilter.class);
		provider.addFilter(OTHER_FILTER_ID,simpleFilter);
	}
	
	@Test
	public void doesFindPropertyFilterWorks(){
		Object o = new Object();
		when(contextFilterFactory.get(o, SpelMultiplePropertyFilter.class)).thenReturn(new SpelMultiplePropertyFilter());
		
		PropertyFilter propertyFilter = provider.findPropertyFilter(FILTER_ID, o);
		assertNotNull(propertyFilter);
		
		assertEquals(simpleFilter, provider.findPropertyFilter(OTHER_FILTER_ID, o));
	}
	
	
	
	
}
