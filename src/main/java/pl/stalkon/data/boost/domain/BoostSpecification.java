package pl.stalkon.data.boost.domain;

import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import data.query.ParameterBinder;


public interface BoostSpecification extends Specification<Object> {
	ParameterBinder getBinder();
	Predicate getPredicate();
	void addViewsToRoot(Root<?> root);
	boolean isDistinct();
	boolean isCountProjection();
}
