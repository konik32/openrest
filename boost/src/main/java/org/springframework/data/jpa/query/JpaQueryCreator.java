/*
 * Copyright 2008-2012 the original author or authors.
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
package org.springframework.data.jpa.query;

import java.util.Iterator;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.query.ParameterMetadataProvider.ParameterMetadata;
import org.springframework.data.jpa.query.OrderAndJoinQueryUtils;
import org.springframework.data.query.parser.AbstractQueryCreator;
import org.springframework.data.query.parser.Part;
import org.springframework.data.query.parser.PartTree;

/**
 * Query creator to create a {@link CriteriaQuery} from a {@link PartTree}.
 * 
 * @author Oliver Gierke
 */
public class JpaQueryCreator extends
		AbstractQueryCreator<CriteriaQuery<Object>, Predicate> {

	private final CriteriaBuilder builder;
	private final Root<?> root;
	private final CriteriaQuery<Object> query;
	private final ParameterMetadataProvider provider;

	/**
	 * Create a new {@link JpaQueryCreator}.
	 * 
	 * @param tree
	 * @param domainClass
	 * @param accessor
	 * @param em
	 */
	public JpaQueryCreator(PartTree tree, Class<?> domainClass,
			CriteriaBuilder builder, ParameterMetadataProvider provider) {

		super(tree);

		this.builder = builder;
		this.query = builder.createQuery().distinct(tree.isDistinct());
		this.root = query.from(domainClass);
		this.provider = provider;
	}

	/**
	 * Returns all {@link javax.persistence.criteria.ParameterExpression}
	 * created when creating the query.
	 * 
	 * @return the parameterExpressions
	 */
	public List<ParameterMetadata<?>> getParameterExpressions() {
		return provider.getExpressions();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.data.repository.query.parser.AbstractQueryCreator
	 * #create(org.springframework.data.repository.query.parser.Part,
	 * java.util.Iterator)
	 */
	@Override
	protected Predicate create(Part part, Iterator<Object> iterator) {

		return toPredicate(part, root);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.data.repository.query.parser.AbstractQueryCreator
	 * #and(org.springframework.data.repository.query.parser.Part,
	 * java.lang.Object, java.util.Iterator)
	 */
	@Override
	protected Predicate and(Part part, Predicate base, Iterator<Object> iterator) {

		return builder.and(base, toPredicate(part, root));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.data.repository.query.parser.AbstractQueryCreator
	 * #or(java.lang.Object, java.lang.Object)
	 */
	@Override
	protected Predicate or(Predicate base, Predicate predicate) {

		return builder.or(base, predicate);
	}

	/**
	 * Finalizes the given {@link Predicate} and applies the given sort.
	 * Delegates to
	 * {@link #complete(Predicate, Sort, CriteriaQuery, CriteriaBuilder)} and
	 * hands it the current {@link CriteriaQuery} and {@link CriteriaBuilder}.
	 */
	@Override
	protected final CriteriaQuery<Object> complete(Predicate predicate,
			Sort sort) {

		return complete(predicate, sort, query, builder, root);
	}

	/**
	 * Template method to finalize the given {@link Predicate} using the given
	 * {@link CriteriaQuery} and {@link CriteriaBuilder}.
	 * 
	 * @param predicate
	 * @param sort
	 * @param query
	 * @param builder
	 * @return
	 */
	protected CriteriaQuery<Object> complete(Predicate predicate, Sort sort,
			CriteriaQuery<Object> query, CriteriaBuilder builder, Root<?> root) {

		CriteriaQuery<Object> select = this.query.select(root).orderBy(
				OrderAndJoinQueryUtils.toOrders(sort, root, builder));
		return predicate == null ? select : select.where(predicate);
	}

	/**
	 * Creates a {@link Predicate} from the given {@link Part}.
	 * 
	 * @param part
	 * @param root
	 * @param iterator
	 * @return
	 */
	private Predicate toPredicate(Part part, Root<?> root) {
		return new PredicateBuilder(part, root, builder, provider).build();
	}

}
