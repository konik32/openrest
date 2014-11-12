package openrest.domain;

import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import data.query.ParameterBinder;


public interface PartTreeSpecification extends Specification<Object> {
	ParameterBinder getBinder();
	void expandRoot(Root<?> root);
	boolean isDistinct();
	boolean isCountProjection();
}
