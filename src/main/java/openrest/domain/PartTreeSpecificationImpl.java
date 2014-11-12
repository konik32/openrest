package openrest.domain;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.mapping.PropertyPath;

import data.jpa.query.JpaQueryCreator;
import data.jpa.query.OrderAndJoinQueryUtils;
import data.jpa.query.ParameterMetadataProvider;
import data.query.CriteriaQueryParameterBinder;
import data.query.JpaParameters;
import data.query.ParameterBinder;
import data.query.ParametersParameterAccessor;
import data.query.parser.Part;
import data.query.parser.PartTree;

public class PartTreeSpecificationImpl implements PartTreeSpecification {

	private PartTree tree;
	private Predicate predicate;
	private ParameterMetadataProvider provider;
	private List<PropertyPath> viewPropertyPaths;

	private JpaParameters jpaParameters;
	private Object[] values;

	public PartTreeSpecificationImpl() {
	}

	public PartTreeSpecificationImpl(PartTree tree, JpaParameters jpaParameters, Object values[], CriteriaBuilder builder, List<PropertyPath> viewPropertyPaths) {
		this.jpaParameters = jpaParameters;
		this.values = values;
		ParametersParameterAccessor accessor = new ParametersParameterAccessor(jpaParameters, values);
		provider = new ParameterMetadataProvider(builder, accessor);
		this.viewPropertyPaths = viewPropertyPaths;
		this.tree = tree;
	}

	public Predicate toPredicate(Root<Object> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
		if (predicate != null) {
			addJoinsToRoot(root);
			return predicate;
		}
		predicate = new JpaQueryCreator(tree, root, cb, provider).createCriteria();
		return predicate;
	}

	@Override
	public void expandRoot(Root<?> root) {
		if (viewPropertyPaths != null) {
			for (PropertyPath path : viewPropertyPaths)
				OrderAndJoinQueryUtils.toRecursiveFetch(path, root);
		}
	}

	private void addJoinsToRoot(Root<?> root) {
		for (Part part : tree.getPartTreeRoot().getParts())
			OrderAndJoinQueryUtils.toExpressionRecursively(root, part.getProperty());
	}

	public ParameterBinder getBinder() {
		return new CriteriaQueryParameterBinder(jpaParameters, values, provider.getExpressions());
	}

	public boolean isDistinct() {
		return tree.isDistinct();
	}

	public boolean isCountProjection() {
		return tree.isCountProjection();
	}

}
