/*
 * Copyright 2008-2014 the original author or authors.
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
package data.jpa.query;

import static javax.persistence.metamodel.Attribute.PersistentAttributeType.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Attribute.PersistentAttributeType;
import javax.persistence.metamodel.Bindable;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.PluralAttribute;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.data.mapping.PropertyPath;
import org.springframework.util.Assert;

/**
 * Modification of {@link QueryUtils} to provide functions not visible from outside of org.springframework.data.jpa.repository.query package.
 * Provides method for adding fetch to root
 * 
 * @author Szymon Konicki
 *
 */
public abstract class OrderAndJoinQueryUtils {

	private static final Map<PersistentAttributeType, Class<? extends Annotation>> ASSOCIATION_TYPES;

	static {
		Map<PersistentAttributeType, Class<? extends Annotation>> persistentAttributeTypes = new HashMap<PersistentAttributeType, Class<? extends Annotation>>();
		persistentAttributeTypes.put(ONE_TO_ONE, OneToOne.class);
		persistentAttributeTypes.put(ONE_TO_MANY, null);
		persistentAttributeTypes.put(MANY_TO_ONE, ManyToOne.class);
		persistentAttributeTypes.put(MANY_TO_MANY, null);
		persistentAttributeTypes.put(ELEMENT_COLLECTION, null);

		ASSOCIATION_TYPES = Collections.unmodifiableMap(persistentAttributeTypes);
	}

	/**
	 * Private constructor to prevent instantiation.
	 */
	private OrderAndJoinQueryUtils() {

	}

	/**
	 * Turns the given {@link Sort} into {@link javax.persistence.criteria.Order}s.
	 * 
	 * @param sort the {@link Sort} instance to be transformed into JPA {@link javax.persistence.criteria.Order}s.
	 * @param root must not be {@literal null}.
	 * @param cb must not be {@literal null}.
	 * @return
	 */
	public static List<javax.persistence.criteria.Order> toOrders(Sort sort, Root<?> root, CriteriaBuilder cb) {

		List<javax.persistence.criteria.Order> orders = new ArrayList<javax.persistence.criteria.Order>();

		if (sort == null) {
			return orders;
		}

		Assert.notNull(root);
		Assert.notNull(cb);

		for (org.springframework.data.domain.Sort.Order order : sort) {
			orders.add(toJpaOrder(order, root, cb));
		}

		return orders;
	}

	/**
	 * Creates a criteria API {@link javax.persistence.criteria.Order} from the given {@link Order}.
	 * 
	 * @param order the order to transform into a JPA {@link javax.persistence.criteria.Order}
	 * @param root the {@link Root} the {@link Order} expression is based on
	 * @param cb the {@link CriteriaBuilder} to build the {@link javax.persistence.criteria.Order} with
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static javax.persistence.criteria.Order toJpaOrder(Order order, Root<?> root, CriteriaBuilder cb) {

		PropertyPath property = PropertyPath.from(order.getProperty(), root.getJavaType());
		Expression<?> expression = toExpressionRecursively(root, property);

		if (order.isIgnoreCase() && String.class.equals(expression.getJavaType())) {
			Expression<String> lower = cb.lower((Expression<String>) expression);
			return order.isAscending() ? cb.asc(lower) : cb.desc(lower);
		} else {
			return order.isAscending() ? cb.asc(expression) : cb.desc(expression);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> Expression<T> toExpressionRecursively(From<?, ?> from, PropertyPath property) {

		Bindable<?> propertyPathModel = null;
		Bindable<?> model = from.getModel();
		String segment = property.getSegment();

		if (model instanceof ManagedType) {

			/*
			 *  Required to keep support for EclipseLink 2.4.x. TODO: Remove once we drop that (probably Dijkstra M1)
			 *  See: https://bugs.eclipse.org/bugs/show_bug.cgi?id=413892
			 */
			propertyPathModel = (Bindable<?>) ((ManagedType<?>) model).getAttribute(segment);
		} else {
			propertyPathModel = from.get(segment).getModel();
		}

		if (requiresJoin(propertyPathModel, model instanceof PluralAttribute)) {
			Join<?, ?> join = getOrCreateJoin(from, segment);
			return (Expression<T>) (property.hasNext() ? toExpressionRecursively(join, property.next()) : join);
		} else {
			Path<Object> path = from.get(segment);
			return (Expression<T>) (property.hasNext() ? toExpressionRecursively(path, property.next()) : path);
		}
	}

	/**
	 * Returns whether the given {@code propertyPathModel} requires the creation of a join. This is the case if we find a
	 * non-optional association.
	 * 
	 * @param propertyPathModel must not be {@literal null}.
	 * @param for
	 * @return
	 */
	private static boolean requiresJoin(Bindable<?> propertyPathModel, boolean forPluralAttribute) {

		if (propertyPathModel == null && forPluralAttribute) {
			return true;
		}

		if (!(propertyPathModel instanceof Attribute)) {
			return false;
		}

		Attribute<?, ?> attribute = (Attribute<?, ?>) propertyPathModel;

		if (!ASSOCIATION_TYPES.containsKey(attribute.getPersistentAttributeType())) {
			return false;
		}

		Class<? extends Annotation> associationAnnotation = ASSOCIATION_TYPES.get(attribute.getPersistentAttributeType());

		if (associationAnnotation == null) {
			return true;
		}

		Member member = attribute.getJavaMember();

		if (!(member instanceof AnnotatedElement)) {
			return true;
		}

		Annotation annotation = AnnotationUtils.getAnnotation((AnnotatedElement) member, associationAnnotation);
		return annotation == null ? true : (Boolean) AnnotationUtils.getValue(annotation, "optional");
	}

	public static Expression<Object> toExpressionRecursively(Path<Object> path, PropertyPath property) {

		Path<Object> result = path.get(property.getSegment());
		return property.hasNext() ? toExpressionRecursively(result, property.next()) : result;
	}

	/**
	 * Returns an existing join for the given attribute if one already exists or creates a new one if not.
	 * 
	 * @param from the {@link From} to get the current joins from.
	 * @param attribute the {@link Attribute} to look for in the current joins.
	 * @return will never be {@literal null}.
	 */
	private static Join<?, ?> getOrCreateJoin(From<?, ?> from, String attribute) {

		for (Fetch<?, ?> join : from.getFetches()) {
			boolean sameName = join.getAttribute().getName().equals(attribute);
			if (sameName && join.getJoinType().equals(JoinType.LEFT)) {
				return (Join<?, ?>) join;
			}
		}
		for (Join<?, ?> join : from.getJoins()) {
			boolean sameName = join.getAttribute().getName().equals(attribute);
			if (sameName && join.getJoinType().equals(JoinType.LEFT)) {
				return join;
			}
		}

		
		return from.join(attribute, JoinType.LEFT);
	}
	

	public static void toRecursiveFetch(PropertyPath property, From<?, ?> from){
		Bindable<?> model = from.getModel();
		String segment = property.getSegment();
		Bindable<?> propertyPathModel = null;
		if (model instanceof ManagedType) {

			/*
			 *  Required to keep support for EclipseLink 2.4.x. TODO: Remove once we drop that (probably Dijkstra M1)
			 *  See: https://bugs.eclipse.org/bugs/show_bug.cgi?id=413892
			 */
			propertyPathModel = (Bindable<?>) ((ManagedType<?>) model).getAttribute(segment);
		} else {
			propertyPathModel = from.get(segment).getModel();
		}
		if (canFetch((Attribute<?,?>)propertyPathModel)) {
			Fetch<?, ?> fetch = getOrCreateFetch(from, segment);
			if(property.hasNext()) toRecursiveFetch( property.next(),(Join) fetch);
		} 		
	}
	
	private static boolean canFetch(Attribute<?, ?> attribute){
		if (!ASSOCIATION_TYPES.containsKey(attribute.getPersistentAttributeType())) {
			return false;
		}
		Class<? extends Annotation> associationAnnotation = ASSOCIATION_TYPES.get(attribute.getPersistentAttributeType());
		if (associationAnnotation == null) {
			return false;
		}
		return true;
	}
	
	private static Fetch<?, ?> getOrCreateFetch(From<?, ?> from, String segment){
		for (Fetch<?, ?> fetch : from.getFetches()) {
			boolean sameName = fetch.getAttribute().getName().equals(segment);
			if (sameName && fetch.getJoinType().equals(JoinType.LEFT)) {
				return (Fetch<?, ?>) fetch;
			}
		}
		return from.fetch(segment, JoinType.LEFT);
	}
}

