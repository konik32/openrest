package pl.stalkon.data.boost.response.filter;

import java.security.Principal;
import java.util.Arrays;
import java.util.HashSet;
import javax.servlet.http.HttpServletRequest;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class SpelMultiplePropertyFilter extends AbstractContextFilter {

	public static final String FILTER_ID = "spelMultiplePropertyFilter";

	private boolean getSpelValue(Object valueToFilter,
			HttpServletRequest request, String spelString) {

		ExpressionParser parser = new SpelExpressionParser();
		Expression exp = parser.parseExpression(spelString);

		EvaluationContext context = new StandardEvaluationContext(
				new ContextWrapper(request, valueToFilter));
		return (Boolean) exp.getValue(context);
	}

	public void prepare(HttpServletRequest request, Object valueToFilter) {
		SpelFilter ep = AnnotationUtils.findAnnotation(
				valueToFilter.getClass(), SpelFilter.class);
		String spelString = ep.value();
		if (!getSpelValue(valueToFilter, request, spelString)) {
			propertiesToIgnore = new HashSet<String>(ep.properties().length);
			propertiesToIgnore.addAll(Arrays.asList(ep.properties()));
		}
	}

	private class ContextWrapper {
		private final HttpServletRequest request;
		private final Principal principal;
		private final Object filteredObject;

		public ContextWrapper(HttpServletRequest request, Object filteredObject) {
			super();
			this.request = request;
			principal = request.getUserPrincipal();
			this.filteredObject = filteredObject;
		}

		public HttpServletRequest getRequest() {
			return request;
		}

		public Object getFilteredObject() {
			return filteredObject;
		}

	}
}
