package pl.openrest.filters.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import pl.openrest.filters.query.PredicateContext;

public interface PredicateContextRepository<T, U extends PredicateContext<?>> {

    T findOne(U predicateContext);

    Iterable<T> findAll(U predicateContext);

    Iterable<T> findAll(U predicateContext, Sort sort);

    Page<T> findAll(U predicateContext, Pageable pageable);

    long count(U predicateContext);

}
