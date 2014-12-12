package openrest.jpa.query;

import java.util.Iterator;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import open.rest.data.query.parser.OpenRestPartTree;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.data.mapping.PropertyPath;

import data.jpa.query.JpaQueryCreator;
import data.jpa.query.OrderAndJoinQueryUtils;
import data.jpa.query.ParameterMetadataProvider;
import data.query.parser.PartTree;

public class OpenRestJpaQueryCreator extends JpaQueryCreator {

	private OpenRestPartTree partTree;

	public OpenRestJpaQueryCreator(OpenRestPartTree tree, Class<?> domainClass, CriteriaBuilder builder, ParameterMetadataProvider provider) {
		super(tree, domainClass, builder, provider);
		this.partTree = tree;
	}

	@Override
	protected Selection<?> getSelection(Root<?> root) {
		if (partTree.isPropertyQuery()) {
			return OrderAndJoinQueryUtils.getOrCreateJoin(root, partTree.getPropertyName());
		}
		return root;
	}

	@Override
	protected void modifyQuery(CriteriaQuery<Object> query, Root<?> root, Predicate predicate, CriteriaBuilder builder) {
		expandRoot(root);
	}

	private void expandRoot(Root<?> root) {
		List<PropertyPath> expandPropertyPath = partTree.getExpandPropertyPaths();
		if (expandPropertyPath != null) {
			for (PropertyPath path : expandPropertyPath) {
				if (partTree.isPropertyQuery())
					OrderAndJoinQueryUtils.toRecursiveFetch(path.next(), OrderAndJoinQueryUtils.getOrCreateJoin(root,partTree.getPropertyName()));
				else
					OrderAndJoinQueryUtils.toRecursiveFetch(path, root);
			}

		}
	}

}