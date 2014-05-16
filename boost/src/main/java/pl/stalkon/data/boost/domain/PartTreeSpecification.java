package pl.stalkon.data.boost.domain;

import java.util.Iterator;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import org.springframework.data.jpa.query.JpaQueryCreator;
import org.springframework.data.jpa.query.OrderAndJoinQueryUtils;
import org.springframework.data.jpa.query.ParameterMetadataProvider;
import org.springframework.data.jpa.query.PredicateBuilder;
import org.springframework.data.mapping.PropertyPath;
import org.springframework.data.query.ParameterBinder;
import org.springframework.data.query.parser.AbstractQueryCreator;
import org.springframework.data.query.parser.OrBranch;
import org.springframework.data.query.parser.Part;
import org.springframework.data.query.parser.PartTree;

public class PartTreeSpecification<T> implements BoostSpecification<T> {

	private final PartTree tree;
	private Predicate predicate;
	private final ParameterMetadataProvider provider;
	private final ParameterBinder binder;
	private final List<PropertyPath> viewPropertyPaths;

	public PartTreeSpecification(PartTree tree,
			ParameterMetadataProvider provider, ParameterBinder binder,
			List<PropertyPath> viewPropertyPaths) {
		this.tree = tree;
		this.provider = provider;
		this.binder = binder;
		this.viewPropertyPaths = viewPropertyPaths;
	}

	public PartTreeSpecification(Predicate predicate, ParameterBinder binder) {
		this.predicate = predicate;
		this.provider = null;
		this.binder = binder;
		this.tree = null;
		this.viewPropertyPaths = null;
	}

	public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query,
			CriteriaBuilder cb) {
		if (predicate != null) {
			addJoinsToRoot(root);
			return predicate;
		}
		if (viewPropertyPaths != null)
			for (PropertyPath path : viewPropertyPaths)
				OrderAndJoinQueryUtils.toRecursiveFetch(path, root);
		predicate = new JpaQueryCreator(tree, root, cb, provider, query)
				.createCriteria();
		return predicate;
	}

	private void addJoinsToRoot(Root<T> root) {
		for (Part part : tree.getPartTreeRoot().getParts())
			OrderAndJoinQueryUtils.toExpressionRecursively(root,
					part.getProperty());
	}

	// private Predicate createPredicate(Root<T> root, CriteriaQuery<?> query,
	// CriteriaBuilder cb) {
	// Predicate base = null;
	// for (OrBranch node : tree) {
	// Predicate currPred = null;
	// for (Part part : node) {
	// currPred = currPred == null ? create(root, cb, part) : and(
	// root, cb, part, currPred);
	// }
	// base = base == null ? currPred : or(cb, base, currPred);
	// }
	// return base;
	// }
	//
	// public Predicate create(Root<T> root, CriteriaBuilder cb, Part part) {
	// return new PredicateBuilder(part, root, cb, provider).build();
	// }
	//
	// public Predicate and(Root<T> root, CriteriaBuilder cb, Part part,
	// Predicate base) {
	// return cb.and(base, create(root, cb, part));
	// }
	//
	// public Predicate or(CriteriaBuilder cb, Predicate base, Predicate
	// predicate) {
	// return cb.or(base, predicate);
	// }

	public ParameterBinder getBinder() {
		return binder;
	}

	public Predicate getPredicate() {
		return predicate;
	}

	public boolean isDistinct() {
		return tree.isDistinct();
	}

	public boolean isCountProjection() {
		return tree.isCountProjection();
	}

}
