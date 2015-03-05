package orest.parser;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import orest.expression.registry.ExpressionEntityInformation;
import orest.expression.registry.ExpressionMethodInformation;
import orest.parser.FilterPart;
import orest.parser.FilterPart.FilterPartType;
import orest.parser.FilterStringParser;
import orest.security.ExpressionEvaluator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
@RunWith(MockitoJUnitRunner.class)
public class FilterStringParserTest {
	
	

	@Mock
	private ExpressionEntityInformation expEntityInfo;

	@Mock
	private ConversionService defaultConversionService;
	
	@Mock
	private ExpressionEvaluator expressionEvaluator;
	
	@InjectMocks
	private FilterStringParser parser;
	
	@Before
	public void setUp(){
		when(expEntityInfo.getMethodInformation(anyString())).thenReturn(mock(ExpressionMethodInformation.class));
		when(expEntityInfo.getMethodInformation("notFound")).thenReturn(null);
		when(expEntityInfo.getMethodInformation(startsWith(" "))).thenReturn(null);
		when(expEntityInfo.getMethodInformation(endsWith(" "))).thenReturn(null);
		when(expEntityInfo.getMethodInformation(startsWith("findBy"))).thenReturn(null);
		when(defaultConversionService.convert(anyObject(),any(TypeDescriptor.class),any(TypeDescriptor.class))).thenReturn(new Object());
		when(expressionEvaluator.processParameter(anyString(), any(Class.class))).thenReturn(new Object());
	}
	
	
	@Test
	public void testIfFormCorrectTree(){
		FilterPart tree = parser.getFilterPart("yearBetween(2;3);and;rankFrom(2;6);or;voteFrom(3;4)", expEntityInfo);
		assertEquals(tree.getMethodInfo(), null);
		assertEquals(tree.getType(), FilterPart.FilterPartType.OR);
		assertEquals(tree.getParts().size(),2);
		for(FilterPart part: tree.getParts()){
			assertEquals(part.getType(), FilterPartType.AND);
			for(FilterPart p: part.getParts()){
				assertEquals(p.getType(), FilterPartType.LEAF);
				assertEquals(p.getParameters().length, 2);
				assertNotNull(p.getMethodInfo());
			}
		}
	}
	
	@Test(expected=FilterParserException.class)
	public void testIfThrowExceptionOnMethodNotFound(){
		parser.getFilterPart("notFound", expEntityInfo);
	}
	
	@Test(expected=FilterParserException.class)
	public void testIfThrowExceptionOnMethodWrongFormat(){
		parser.getFilterPart("yearBetween(2;3", expEntityInfo);
	}
	@Test
	public void testIfDoesNotThrowExceptionOnSingleParam(){
		parser.getFilterPart("yearBetween(2;)", expEntityInfo);
		parser.getFilterPart("yearBetween(;2)", expEntityInfo);
	}
	@Test
	public void testIFNullOnNullOrEmptyFilterString(){
		assertNull(parser.getFilterPart("", expEntityInfo));
		assertNull(parser.getFilterPart(null, expEntityInfo));
	}
	
	@Test(expected=FilterParserException.class)
	public void testIfRemovesFindByPrefix(){
		assertNotNull(parser.getSearchFilterPart("findByName", expEntityInfo));
	}
	
	@Test
	public void testIfTrimWhiteSpacesNearMethodNames(){
		FilterPart tree = parser.getFilterPart("yearBetween (2;3) ;and;  rankBetween(2;3)", expEntityInfo);
		assertNotNull(tree.getParts().get(0).getParts().get(0).getMethodInfo());
		assertNotNull(tree.getParts().get(0).getParts().get(1).getMethodInfo());
	}
	
	@Test
	public void testIfDoNotTrimWhiteSpacesInParameters(){
		FilterPart tree = parser.getFilterPart("like(John doe; John doe)", expEntityInfo);
		String arr[] = tree.getParts().get(0).getParts().get(0).getParameters();
		assertEquals("John doe", arr[0]);
		assertEquals(" John doe", arr[1]);
	}
	
	
	

}
