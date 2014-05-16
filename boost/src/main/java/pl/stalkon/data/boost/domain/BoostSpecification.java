package pl.stalkon.data.boost.domain;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.query.ParameterBinder;


public interface BoostSpecification<T> extends Specification<T> {
	ParameterBinder getBinder();
	Predicate getPredicate();
	boolean isDistinct();
	boolean isCountProjection();
}
