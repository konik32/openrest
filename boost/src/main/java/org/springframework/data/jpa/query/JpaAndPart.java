package org.springframework.data.jpa.query;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class JpaAndPart implements PartCollection {

	public Predicate toPredicate(Root<?> root, CriteriaQuery<?> query,
			CriteriaBuilder cb, ParameterMetadataProvider provider) {
		// TODO Auto-generated method stub
		return null;
	}

}
