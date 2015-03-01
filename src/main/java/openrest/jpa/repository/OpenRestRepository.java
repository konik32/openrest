package openrest.jpa.repository;

import openrest.query.parameter.QueryParameterHolder;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public interface OpenRestRepository {

	public Iterable<Object> findAll(QueryParameterHolder spec, Class<Object> domainClass);

	// public Long getCount(QueryParameterHolder spec,
	// Class<Object> domainClass);

	public Object findOne(QueryParameterHolder spec, Class<Object> domainClass);
}
