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
public class JpaQueryCreator extends AbstractQueryCreator<Predicate> {

	private final CriteriaBuilder builder;
	private final Root<?> root;
	private final CriteriaQuery<?> query;
	private final ParameterMetadataProvider provider;

	/**
	 * Create a new {@link JpaQueryCreator}.
	 * 
	 * @param tree
	 * @param domainClass
	 * @param accessor
	 * @param em
	 */
	public JpaQueryCreator(PartTree tree, Root<?> root, CriteriaBuilder builder, ParameterMetadataProvider provider, CriteriaQuery<?> query) {
		super(tree);
		this.builder = builder;
		this.query = query;
		this.root = root;
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

}
