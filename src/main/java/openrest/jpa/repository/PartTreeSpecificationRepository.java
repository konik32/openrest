package openrest.jpa.repository;

import openrest.domain.OpenRestQueryParameterHolder;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public interface PartTreeSpecificationRepository {

	public Iterable<Object> findAll(OpenRestQueryParameterHolder spec, Class<Object> domainClass);

	// public Long getCount(OpenRestQueryParameterHolder spec,
	// Class<Object> domainClass);

	public Object findOne(OpenRestQueryParameterHolder spec, Class<Object> domainClass);
}
