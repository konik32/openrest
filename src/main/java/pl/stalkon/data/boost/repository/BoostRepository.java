package pl.stalkon.data.boost.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import pl.stalkon.data.boost.domain.BoostSpecification;

public interface BoostRepository {
	public Iterable<Object> findAll(BoostSpecification spec,
			Class<Object> domainClass, Pageable pageable, Sort sort);
}
