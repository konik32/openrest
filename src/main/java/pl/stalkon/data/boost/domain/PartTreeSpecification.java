package pl.stalkon.data.boost.domain;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.springframework.data.mapping.PropertyPath;

import pl.stalkon.data.jpa.query.JpaQueryCreator;
import pl.stalkon.data.jpa.query.OrderAndJoinQueryUtils;
import pl.stalkon.data.jpa.query.ParameterMetadataProvider;
import pl.stalkon.data.query.ParameterBinder;
import pl.stalkon.data.query.parser.Part;
import pl.stalkon.data.query.parser.PartTree;

public class PartTreeSpecification implements BoostSpecification {

	private PartTree tree;
	private Predicate predicate;
	private ParameterMetadataProvider provider;
	private ParameterBinder binder;
	private List<PropertyPath> viewPropertyPaths;

	public PartTreeSpecification() {
	}

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

	public Predicate toPredicate(Root<Object> root, CriteriaQuery<?> query,
			CriteriaBuilder cb) {
		if (predicate != null) {
			addJoinsToRoot(root);
			return predicate;
		}
		predicate = new JpaQueryCreator(tree, root, cb, provider, query)
				.createCriteria();
		return predicate;
	}

	@Override
	public void addViewsToRoot(Root<?> root) {
		if (viewPropertyPaths != null)
			for (PropertyPath path : viewPropertyPaths)
				OrderAndJoinQueryUtils.toRecursiveFetch(path, root);
	}

	private void addJoinsToRoot(Root<?> root) {
		for (Part part : tree.getPartTreeRoot().getParts())
			OrderAndJoinQueryUtils.toExpressionRecursively(root,
					part.getProperty());
	}

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
