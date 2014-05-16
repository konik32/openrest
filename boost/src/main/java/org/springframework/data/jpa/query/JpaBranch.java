package org.springframework.data.jpa.query;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public abstract class JpaBranch implements JpaTreePart {
	protected final List<JpaTreePart> treeParts;

	public JpaBranch(List<JpaTreePart> treeParts) {
		this.treeParts = treeParts;
	}

	public Predicate create(ParameterMetadataProvider provider, Root<?> root,
			CriteriaQuery<?> query, CriteriaBuilder cb, JpaTreePart part) {
		return part.toPredicate(provider, root, query, cb);
	}

	public Predicate toPredicate(ParameterMetadataProvider provider,
			Root<?> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
		Predicate base = null;
		for (JpaTreePart part : treeParts) {
			base = base == null ? create(provider, root, query, cb, part)
					: branchToPredicate(provider, root, query, cb, base,part);
		}
		return base;
	}

	protected abstract Predicate branchToPredicate(
			ParameterMetadataProvider provider, Root<?> root,
			CriteriaQuery<?> query, CriteriaBuilder cb, Predicate base,JpaTreePart part);
}
