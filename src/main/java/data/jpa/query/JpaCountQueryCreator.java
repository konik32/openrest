package data.jpa.query;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import org.springframework.data.domain.Sort;

import data.query.parser.PartTree;

public class JpaCountQueryCreator extends JpaQueryCreator {

	/**
	 * Creates a new {@link JpaCountQueryCreator}.
	 * 
	 * @param tree
	 * @param domainClass
	 * @param parameters
	 * @param em
	 */
	public JpaCountQueryCreator(PartTree tree, Class<?> domainClass, CriteriaBuilder builder, ParameterMetadataProvider provider) {
		super(tree, domainClass, builder, provider);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.data.jpa.repository.query.JpaQueryCreator#complete
	 * (javax.persistence.criteria.Predicate,
	 * org.springframework.data.domain.Sort,
	 * javax.persistence.criteria.CriteriaQuery,
	 * javax.persistence.criteria.CriteriaBuilder,
	 * javax.persistence.criteria.Root)
	 */
	@Override
	protected CriteriaQuery<Object> complete(Predicate predicate, Sort sort, CriteriaQuery<Object> query, CriteriaBuilder builder, Root<?> root) {
		Expression<?> sel = (Expression<?>) getSelection(root);
		Selection<?> selection = tree.isDistinct() ? builder.countDistinct(sel) : builder.count(sel);
		CriteriaQuery<Object> select = query.select(selection);
		return predicate == null ? select : select.where(predicate);
	}

	@Override
	protected Selection<?> getSelection(Root<?> root) {
		return root;
	}
}
