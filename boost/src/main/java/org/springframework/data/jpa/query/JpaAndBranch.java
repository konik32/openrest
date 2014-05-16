package org.springframework.data.jpa.query;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.query.parser.Part;
import org.springframework.data.query.parser.Part.Type;


public class JpaAndBranch extends JpaBranch {

	public JpaAndBranch(List<JpaTreePart> treeParts) {
		super(treeParts);
	}

	@Override
	protected Predicate branchToPredicate(ParameterMetadataProvider provider,
			Root<?> root, CriteriaQuery<?> query, CriteriaBuilder cb,
			Predicate base,JpaTreePart part) {
		return cb.and(base, part.toPredicate(provider, root, query, cb));
	}




}
