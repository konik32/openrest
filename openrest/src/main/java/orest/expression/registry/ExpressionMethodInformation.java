package orest.expression.registry;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

import org.springframework.data.repository.query.Param;
import org.springframework.hateoas.core.AnnotationAttribute;
import org.springframework.hateoas.core.MethodParameters;

import com.mysema.commons.lang.Assert;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathBuilder;

@Data
public class ExpressionMethodInformation {

	private static final AnnotationAttribute PARAM_VALUE = new AnnotationAttribute(
			Param.class);

	public enum MethodType {
		SEARCH, FILTER
	}

	private String name;
	private MethodParameters methodParameters;
	private Method method;
	private MethodType methodType;
	private boolean defaultedPageable;
	private StaticFilter staticFilter;
	private List<Join> joins;

	
	
	@Data
	public static class StaticFilter {
		private final String condition;
		private final String[] parameters;
	}
	
	@Data
	public static class Join {
		private final Path<?> path;
		private final boolean collection;
		private final boolean fetch;
		private final Class<?> type;
	}
}
