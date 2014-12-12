package openrest.jpa.query;

import java.util.Iterator;
import java.util.Set;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import org.springframework.data.domain.Sort;

import open.rest.data.query.parser.OpenRestPartTree;
import data.jpa.query.JpaCountQueryCreator;
import data.jpa.query.OrderAndJoinQueryUtils;
import data.jpa.query.ParameterMetadataProvider;
import data.query.parser.PartTree;

public class OpenRestJpaCountQueryCreator extends JpaCountQueryCreator {

	public OpenRestJpaCountQueryCreator(OpenRestPartTree tree, Class<?> domainClass, CriteriaBuilder builder, ParameterMetadataProvider provider) {
		super(tree, domainClass, builder, provider);
	}

	@Override
	protected Selection<?> getSelection(Root<?> root) {
		OpenRestPartTree partTree = (OpenRestPartTree) tree;
		if (partTree.isPropertyQuery()) {
			return OrderAndJoinQueryUtils.getOrCreateJoin(root, partTree.getPropertyName());
		}
		return root;
	}
}
