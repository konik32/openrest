package org.springframework.data.jpa.query;


import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;


public interface JpaTreePart {
	Predicate toPredicate(ParameterMetadataProvider provider, Root<?> root, CriteriaQuery<?> query,
			CriteriaBuilder cb);

}
