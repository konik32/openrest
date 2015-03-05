package orest.parser;

import org.apache.commons.lang3.text.WordUtils;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import orest.expression.registry.ExpressionEntityInformation;
import orest.expression.registry.ExpressionMethodInformation;
import orest.expression.registry.ExpressionMethodInformation.MethodType;
import orest.parser.FilterPart.FilterPartType;

@RequiredArgsConstructor
public class FilterStringParser {

	private static final String OR_SPLITTER = ";or;";
	private static final String AND_SPLITTER = ";and;";
	private static final String SEARCH_METHOD_PREFIX = "findBy";
	private static final String PARAMS_PATTERN = "\\(.+\\)";

	/**
	 * Extract expression methods and parameters from filters string like
	 * yearBetween(10;20);and;rankGt(3);or;rankLe(6).
	 * 
	 * @param filters
	 * @param expEntityInfo
	 * @return Tree composed of {@link FilterPart}s. Root part is type of
	 *         {@link FilterPart.FilterPartType.OR}
	 */
	public FilterPart getFilterPart(String filters, ExpressionEntityInformation expEntityInfo) {
		if (filters == null || filters.isEmpty())
			return null;
		FilterPart filterOrPart = new FilterPart(FilterPartType.OR);
		for (String orPart : filters.split(OR_SPLITTER)) {
			FilterPart filterAndPart = new FilterPart(FilterPartType.AND);
			for (String andPart : orPart.split(AND_SPLITTER)) {
				MethodParts methodParts = getMethodParts(andPart);
				ExpressionMethodInformation expressionMethodInformation = getExpressionMethodInformation(methodParts.name, expEntityInfo);
				filterAndPart.addPart(new FilterPart(expressionMethodInformation, methodParts.getParameters()));
			}
			filterOrPart.addPart(filterAndPart);
		}
		return filterOrPart;
	}

	/**
	 * Extract expression and parameters from search method name. Removes findBy
	 * prefix.
	 * 
	 * @param name
	 * @param expEntityInfo
	 * @return
	 */
	public FilterPart getSearchFilterPart(String name, ExpressionEntityInformation expEntityInfo) {
		if (name == null)
			return null;
		name = name.trim();
		if (name.startsWith(SEARCH_METHOD_PREFIX))
			name = name.replace(SEARCH_METHOD_PREFIX, "");
		MethodParts methodParts = getMethodParts(name);
		ExpressionMethodInformation methodInformation = getExpressionMethodInformation(methodParts.getName(), expEntityInfo);
		if (methodInformation.getMethodType() != MethodType.SEARCH)
			throw new FilterParserException(name + " is not search method");
		return new FilterPart(methodInformation, methodParts.parameters);
	}
	
	
	private ExpressionMethodInformation getExpressionMethodInformation(String name, ExpressionEntityInformation expEntityInfo) {
		name = WordUtils.uncapitalize(name);
		ExpressionMethodInformation methodInfo = expEntityInfo.getMethodInformation(name);
		if (methodInfo == null)
			throw new FilterParserException(name + " method not found");
		return methodInfo;
	}

	private MethodParts getMethodParts(String method) {
		method = method.trim();
		int bracketIndex = method.indexOf("(");
		if (bracketIndex == -1)
			return new MethodParts(method, null);
		String name = method.substring(0, bracketIndex).trim();
		String paramsString = method.substring(bracketIndex);
		if (!paramsString.matches(PARAMS_PATTERN))
			throw new FilterParserException(method + " has wrong format");
		paramsString = paramsString.replaceAll("[\\(\\)]", "");
		return new MethodParts(name, paramsString.split(";"));
	}

	@Data
	private class MethodParts {
		public final String name;
		public final String[] parameters;
	}
}
