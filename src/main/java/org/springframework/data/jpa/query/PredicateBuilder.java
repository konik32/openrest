package org.springframework.data.jpa.query;

import java.util.Collection;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.query.ParameterMetadataProvider.ParameterMetadata;
import org.springframework.data.mapping.PropertyPath;
import org.springframework.data.query.parser.Part;
import org.springframework.data.query.parser.Part.Type;
import org.springframework.util.Assert;

/**
 * Simple builder to contain logic to create JPA {@link Predicate}s from
 * {@link Part}s.
 * 
 * @author Phil Webb
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class PredicateBuilder {

	private final Part part;
	private final Root<?> root;
	private final CriteriaBuilder builder;
	private final ParameterMetadataProvider provider;

	/**
	 * Creates a new {@link PredicateBuilder} for the given {@link Part} and
	 * {@link Root}.
	 * 
	 * @param part
	 *            must not be {@literal null}.
	 * @param root
	 *            must not be {@literal null}.
	 */
	public PredicateBuilder(Part part, Root<?> root, CriteriaBuilder builder,
			ParameterMetadataProvider provider) {

		Assert.notNull(part);
		Assert.notNull(root);
		Assert.notNull(builder);
		Assert.notNull(provider);
		this.part = part;
		this.root = root;
		this.builder = builder;
		this.provider = provider;
	}

	/**
	 * Returns a path to a {@link Comparable}.
	 * 
	 * @param root
	 * @param part
	 * @return
	 */
	@SuppressWarnings({ "rawtypes" })
	private Expression<? extends Comparable> getComparablePath(Root<?> root,
			Part part) {

		return getTypedPath(root, part);
	}

	private <T> Expression<T> getTypedPath(Root<?> root, Part part) {
		return OrderAndJoinQueryUtils.toExpressionRecursively(root,
				part.getProperty());
	}

	/**
	 * Builds a JPA {@link Predicate} from the underlying {@link Part}.
	 * 
	 * @return
	 */
	public Predicate build() {

		PropertyPath property = part.getProperty();
		Expression<Object> path = OrderAndJoinQueryUtils
				.toExpressionRecursively(root, property);

		switch (part.getType()) {
		case BETWEEN:
			ParameterMetadata<Comparable> first = provider.next(part);
			ParameterMetadata<Comparable> second = provider.next(part);
			return builder.between(getComparablePath(root, part),
					first.getExpression(), second.getExpression());
		case AFTER:
		case GREATER_THAN:
			return builder.greaterThan(getComparablePath(root, part), provider
					.next(part, Comparable.class).getExpression());
		case GREATER_THAN_EQUAL:
			return builder.greaterThanOrEqualTo(getComparablePath(root, part),
					provider.next(part, Comparable.class).getExpression());
		case BEFORE:
		case LESS_THAN:
			return builder.lessThan(getComparablePath(root, part), provider
					.next(part, Comparable.class).getExpression());
		case LESS_THAN_EQUAL:
			return builder.lessThanOrEqualTo(getComparablePath(root, part),
					provider.next(part, Comparable.class).getExpression());
		case IS_NULL:
			return path.isNull();
		case IS_NOT_NULL:
			return path.isNotNull();
		case NOT_IN:
			return path.in(
					provider.next(part, Collection.class).getExpression())
					.not();
		case IN:
			return path.in(provider.next(part, Collection.class)
					.getExpression());
		case STARTING_WITH:
		case ENDING_WITH:
		case CONTAINING:
		case LIKE:
		case NOT_LIKE:
			Expression<String> stringPath = getTypedPath(root, part);
			Expression<String> propertyExpression = upperIfIgnoreCase(stringPath);
			Expression<String> parameterExpression = upperIfIgnoreCase(provider
					.next(part, String.class).getExpression());
			Predicate like = builder.like(propertyExpression,
					parameterExpression);
			return part.getType() == Type.NOT_LIKE ? like.not() : like;
		case TRUE:
			Expression<Boolean> truePath = getTypedPath(root, part);
			return builder.isTrue(truePath);
		case FALSE:
			Expression<Boolean> falsePath = getTypedPath(root, part);
			return builder.isFalse(falsePath);
		case SIMPLE_PROPERTY:
			ParameterMetadata<Object> expression = provider.next(part);
			return expression.isIsNullParameter() ? path.isNull() : builder
					.equal(upperIfIgnoreCase(path),
							upperIfIgnoreCase(expression.getExpression()));
		case NEGATING_SIMPLE_PROPERTY:
			return builder.notEqual(upperIfIgnoreCase(path),
					upperIfIgnoreCase(provider.next(part).getExpression()));
		default:
			throw new IllegalArgumentException("Unsupported keyword "
					+ part.getType());
		}
	}

	/**
	 * Applies an {@code UPPERCASE} conversion to the given {@link Expression}
	 * in case the underlying {@link Part} requires ignoring case.
	 * 
	 * @param expression
	 *            must not be {@literal null}.
	 * @return
	 */
	private <T> Expression<T> upperIfIgnoreCase(
			Expression<? extends T> expression) {

		switch (part.shouldIgnoreCase()) {
		case ALWAYS:
			Assert.state(canUpperCase(expression), "Unable to ignore case of "
					+ expression.getJavaType().getName()
					+ " types, the property '"
					+ part.getProperty().getSegment()
					+ "' must reference a String");
			return (Expression<T>) builder
					.upper((Expression<String>) expression);
		case WHEN_POSSIBLE:
			if (canUpperCase(expression)) {
				return (Expression<T>) builder
						.upper((Expression<String>) expression);
			}
		}
		return (Expression<T>) expression;
	}

	private boolean canUpperCase(Expression<?> expression) {
		return String.class.equals(expression.getJavaType());
	}
}