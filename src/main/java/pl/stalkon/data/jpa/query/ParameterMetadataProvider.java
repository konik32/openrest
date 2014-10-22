/*
 * Copyright 2011-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package pl.stalkon.data.jpa.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.ParameterExpression;

import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

import pl.stalkon.data.query.Parameter;
import pl.stalkon.data.query.Parameters;
import pl.stalkon.data.query.ParametersParameterAccessor;
import pl.stalkon.data.query.parser.Part;
import pl.stalkon.data.query.parser.Part.Type;

/**
 * Helper class to allow easy creation of {@link ParameterMetadata}s.
 * 
 * @author Oliver Gierke
 */
public class ParameterMetadataProvider {

	private final CriteriaBuilder builder;
	private final Iterator<? extends Parameter> parameters;
	private final List<ParameterMetadata<?>> expressions;
	private Iterator<Object> accessor;

	/**
	 * Creates a new {@link ParameterMetadataProvider} from the given {@link CriteriaBuilder} and
	 * {@link ParametersParameterAccessor}.
	 * 
	 * @param builder must not be {@literal null}.
	 * @param parameters must not be {@literal null}.
	 */
	public ParameterMetadataProvider(CriteriaBuilder builder, ParametersParameterAccessor accessor) {

		this(builder, accessor.getParameters());
		Assert.notNull(accessor);
		this.accessor = accessor.iterator();
	}

	public ParameterMetadataProvider(CriteriaBuilder builder, Parameters<?, ?> parameters) {

		Assert.notNull(builder);

		this.builder = builder;
		this.parameters = parameters.getBindableParameters().iterator();
		this.expressions = new ArrayList<ParameterMetadata<?>>();
		this.accessor = null;
	}

	/**
	 * Returns all {@link ParameterMetadata}s built.
	 * 
	 * @return the expressions
	 */
	public List<ParameterMetadata<?>> getExpressions() {
		return Collections.unmodifiableList(expressions);
	}

	/**
	 * Builds a new {@link ParameterMetadata} for given {@link Part} and the next {@link Parameter}.
	 * 
	 * @param <T>
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> ParameterMetadata<T> next(Part part) {

		Parameter parameter = parameters.next();
		return (ParameterMetadata<T>) next(part, parameter.getType(), parameter.getName());
	}

	/**
	 * Builds a new {@link ParameterMetadata} of the given {@link Part} and type. Forwards the underlying
	 * {@link Parameters} as well.
	 * 
	 * @param <T>
	 * @param type must not be {@literal null}.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> ParameterMetadata<? extends T> next(Part part, Class<T> type) {

		Parameter parameter = parameters.next();
		Class<?> typeToUse = ClassUtils.isAssignable(type, parameter.getType()) ? parameter.getType() : type;
		return (ParameterMetadata<? extends T>) next(part, typeToUse, parameter.getName());
	}

	/**
	 * Builds a new {@link ParameterMetadata} for the given type and name.
	 * 
	 * @param <T>
	 * @param part must not be {@literal null}.
	 * @param type must not be {@literal null}.
	 * @param name
	 * @return
	 */
	private <T> ParameterMetadata<T> next(Part part, Class<T> type, String name) {

		Assert.notNull(type);

		ParameterExpression<T> expression = name == null ? builder.parameter(type) : builder.parameter(type, name);
		ParameterMetadata<T> value = new ParameterMetadata<T>(expression, part.getType(),
				accessor == null ? ParameterMetadata.PLACEHOLDER : accessor.next());
		expressions.add(value);

		return value;
	}

	static class ParameterMetadata<T> {

		static final Object PLACEHOLDER = new Object();

		private final ParameterExpression<T> expression;
		private final Type type;

		public ParameterMetadata(ParameterExpression<T> expression, Type type, Object value) {

			this.expression = expression;
			this.type = value == null && Type.SIMPLE_PROPERTY.equals(type) ? Type.IS_NULL : type;
		}

		/**
		 * Returns the {@link ParameterExpression}.
		 * 
		 * @return the expression
		 */
		public ParameterExpression<T> getExpression() {
			return expression;
		}

		/**
		 * Returns whether the parameter shall be considered an {@literal IS NULL} parameter.
		 * 
		 * @return
		 */
		public boolean isIsNullParameter() {
			return Type.IS_NULL.equals(type);
		}

		/**
		 * Prepares the object before it's actually bound to the {@link javax.persistence.Query;}.
		 * 
		 * @param parameter must not be {@literal null}.
		 * @return
		 */
		public Object prepare(Object parameter) {

			Assert.notNull(parameter);

			switch (type) {
				case STARTING_WITH:
					return String.format("%s%%", parameter.toString());
				case ENDING_WITH:
					return String.format("%%%s", parameter.toString());
				case CONTAINING:
					return String.format("%%%s%%", parameter.toString());
				default:
					return Collection.class.equals(expression.getJavaType()) ? toCollection(parameter) : parameter;
			}
		}

		/**
		 * Return sthe given argument as {@link Collection} which means it will return it as is if it's a
		 * {@link Collections}, turn an array into an {@link ArrayList} or simply wrap any other value into a single element
		 * {@link Collections}.
		 * 
		 * @param value
		 * @return
		 */
		private static Collection<?> toCollection(Object value) {

			if (value == null) {
				return null;
			}

			if (value instanceof Collection) {
				return (Collection<?>) value;
			}

			if (ObjectUtils.isArray(value)) {
				return Arrays.asList(ObjectUtils.toObjectArray(value));
			}

			return Collections.singleton(value);
		}
	}
}
