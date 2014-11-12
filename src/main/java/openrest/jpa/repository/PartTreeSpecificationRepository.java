package openrest.jpa.repository;

import openrest.domain.PartTreeSpecification;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public interface PartTreeSpecificationRepository {
	public Iterable<Object> findAll(PartTreeSpecification spec,
			Class<Object> domainClass, Pageable pageable, Sort sort);
}
