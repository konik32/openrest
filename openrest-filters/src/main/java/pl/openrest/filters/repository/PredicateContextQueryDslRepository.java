package pl.openrest.filters.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.QSort;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import pl.openrest.filters.predicate.annotation.PredicateRepository;
import pl.openrest.filters.query.PredicateContext;
/**
 * This repository has to be declared for every entity for which {@link PredicateRepository} has been declared.
 * @author Szymon Konicki
 *
 * @param <T> Entity type
 */
public interface PredicateContextQueryDslRepository<T> extends QueryDslPredicateExecutor<T> {
	
	T findOne(PredicateContext predicateContext);
	Iterable<T> findAll(PredicateContext predicateContext);
	Iterable<T> findAll(PredicateContext predicateContext, QSort sort);
	Page<T> findAll(PredicateContext predicateContext,Pageable pageable);
	long count(PredicateContext predicateContext);
	
}
