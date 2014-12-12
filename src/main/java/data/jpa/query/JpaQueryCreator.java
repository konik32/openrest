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
package data.jpa.query;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;





import javax.persistence.criteria.Selection;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.query.QueryUtils;

import data.jpa.query.ParameterMetadataProvider.ParameterMetadata;
import data.query.parser.AbstractQueryCreator;
import data.query.parser.Part;
import data.query.parser.PartTree;

/**
 * Modification of
 * {@link org.springframework.data.jpa.repository.query.JpaQueryCreator} to use
 * {@link ParameterMetadataProvider}.
 * 
 * @author Szymon Konicki
 *
 */
public class JpaQueryCreator extends AbstractQueryCreator<CriteriaQuery<Object>, Predicate> {

	protected final CriteriaBuilder builder;
	protected final Root<?> root;
	protected final CriteriaQuery<Object> query;
	protected final ParameterMetadataProvider provider;

	/**
	 * Create a new {@link JpaQueryCreator}.
	 * 
	 * @param tree
	 * @param domainClass
	 * @param accessor
	 * @param em
	 */
	public JpaQueryCreator(PartTree tree, Class<?> domainClass, CriteriaBuilder builder,
			ParameterMetadataProvider provider) {
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

	@Override
	protected Predicate or(Predicate base, Predicate predicate) {
		return builder.or(base, predicate);
	}

	@Override
	protected Predicate create(Part part) {
		return new PredicateBuilder(part, root, builder, provider).build();
	}

	@Override
	protected Predicate and(Predicate base, Predicate criteria) {
		return builder.and(base, criteria);
	}

	/**
	 * Finalizes the given {@link Predicate} and applies the given sort. Delegates to
	 * {@link #complete(Predicate, Sort, CriteriaQuery, CriteriaBuilder)} and hands it the current {@link CriteriaQuery}
	 * and {@link CriteriaBuilder}.
	 */
	@Override
	protected final CriteriaQuery<Object> complete(Predicate predicate, Sort sort) {
		return complete(predicate, sort, query, builder, root);
	}

	/**
	 * Template method to finalize the given {@link Predicate} using the given {@link CriteriaQuery} and
	 * {@link CriteriaBuilder}.
	 * 
	 * @param predicate
	 * @param sort
	 * @param query
	 * @param builder
	 * @return
	 */
	protected CriteriaQuery<Object> complete(Predicate predicate, Sort sort, CriteriaQuery<Object> query,
			CriteriaBuilder builder, Root<?> root) {
		modifyQuery(query, root, predicate, builder);
		CriteriaQuery<Object> select = this.query.select(getSelection(root)).orderBy(QueryUtils.toOrders(sort, root, builder));
		return predicate == null ? select : select.where(predicate);
	}
	
	protected Selection<?> getSelection(Root<?> root){
		return root;
	}
	
	protected void modifyQuery(CriteriaQuery<Object> query, Root<?> root, Predicate predicate, CriteriaBuilder builder){
		
	}

}
