package pl.stalkon.data.boost.domain;

import javax.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.query.ParameterBinder;


public interface BoostSpecification extends Specification<Object> {
	ParameterBinder getBinder();
	Predicate getPredicate();
	boolean isDistinct();
	boolean isCountProjection();
}
