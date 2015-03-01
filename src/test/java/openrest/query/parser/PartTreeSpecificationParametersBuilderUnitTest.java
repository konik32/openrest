package openrest.query.parser;

import com.fasterxml.jackson.databind.ObjectMapper;

public class PartTreeSpecificationParametersBuilderUnitTest {

	private ObjectMapper objectMapper = new ObjectMapper();

//	@Test
//	public void testAppendLeafTempPart() {
//		QueryParametersHolderBuilder builder = createAppendAndReturn("between", "id", 2, new String[]{"1", "2"});
////		assertEquals(builder.getJpaParameters().size(), 2);
////		assertEquals(builder.getParametersValues().size(), 2);
//	}
	
//	@Test(expected=RequestParsingException.class)
//	public void doesThrowExceptionOnInvalidFunctionName(){
//		createAppendAndReturn("test", "id", 2, new String[]{"1", "2"});
//	}
//	
//	@Test(expected=RequestParsingException.class)
//	public void doesThrowExceptionOnInvalidFunctionParametersCount(){
//		createAppendAndReturn("between", "id", 5, new String[]{"1", "2"});
//	}
//	
//	@Test(expected=RequestParsingException.class)
//	public void doesThrowExceptionOnTwoManyParametersValues(){
//		createAppendAndReturn("between", "id", 2, new String[]{"1","2","3"});
//	}
//	
//	@Test(expected=RequestParsingException.class)
//	public void doesThrowExceptionOnNotEnoughParametersValues(){
//		createAppendAndReturn("between", "id", 2, new String[]{"1"});
//	}
//	
//	private QueryParametersHolderBuilder createAppendAndReturn(String functionName, String propertyName,int paramsCount, String[] parameters){
//		QueryParametersHolderBuilder builder = new QueryParametersHolderBuilder(Product.class, objectMapper);
//		TempPart tempPart = new TempPart(functionName, propertyName, paramsCount);
//		List<String[]> params = new ArrayList<String[]>();
//		params.add(parameters);
//		builder.append(tempPart, params);
//		return builder;
//	}
}
