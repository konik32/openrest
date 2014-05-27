/*
 * Copyright 2008-2013 the original author or authors.
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
package org.springframework.data.query.parser;


import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.ParametersParameterAccessor;
import org.springframework.util.Assert;

public abstract class AbstractQueryCreator<S> {

	protected final PartTree tree;

	/**
	 * Creates a new {@link AbstractQueryCreator} for the given {@link PartTree}
	 * and {@link ParametersParameterAccessor}. The latter is used to hand
	 * actual parameter values into the callback methods as well as to apply
	 * dynamic sorting via a {@link Sort} parameter.
	 * 
	 * @param tree
	 *            must not be {@literal null}.
	 * @param parameters
	 *            can be {@literal null}.
	 */
	public AbstractQueryCreator(PartTree tree) {
		Assert.notNull(tree, "PartTree must not be null");
		this.tree = tree;
	}


	/**
	 * Actual query building logic. Traverses the {@link PartTree} and invokes
	 * callback methods to delegate actual criteria creation and concatenation.
	 * 
	 * @param tree
	 * @return
	 */
	public S createCriteria() {
		return toRecursiveCriteria(tree.getPartTreeRoot());
	}

	private S toRecursiveCriteria(TreePart part) {
		if (part instanceof TreeBranch) {
			TreeBranch branch = (TreeBranch) part;
			S criteria = null;
			for (TreePart p : branch.treeParts) {
				criteria = criteria == null ? toRecursiveCriteria(p) : decide(
						part, criteria, toRecursiveCriteria(p));
			}
			return criteria;
		} else {
			return create((Part) part);
		}
	}

	private S decide(TreePart part, S base, S criteria) {
		if (part instanceof AndBranch)
			return and(base, criteria);
		return or(base, criteria);
	}

	/**
	 * Creates a new atomic instance of the criteria object.
	 * 
	 * @param part
	 * @param iterator
	 * @return
	 */
	protected abstract S create(Part part);

	/**
	 * Creates a new criteria object from the given part and and-concatenates it
	 * to the given base criteria.
	 * 
	 * @param part
	 * @param base
	 *            will never be {@literal null}.
	 * @param iterator
	 * @return
	 */
	protected abstract S and(S base, S criteria);

	/**
	 * Or-concatenates the given base criteria to the given new criteria.
	 * 
	 * @param base
	 * @param criteria
	 * @return
	 */
	protected abstract S or(S base, S criteria);

}
