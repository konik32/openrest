package org.springframework.data.jpa.query;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.query.parser.Part;

public class JpaLeaf extends Part implements JpaTreePart {

	public JpaLeaf(String path, Type type, Class<?> domainClass) {
		super(path,type,domainClass);
	}
	
	public JpaLeaf(String path,Type type, Class<?> domainClass, boolean alwaysIgnoreCase){
		super(path, type, domainClass, alwaysIgnoreCase);
	}
	
	public Predicate toPredicate(ParameterMetadataProvider provider,
			Root<?> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
		PredicateBuilder predicateBuilder = new PredicateBuilder(this, root, cb, provider);
		return predicateBuilder.build();
	}

}
