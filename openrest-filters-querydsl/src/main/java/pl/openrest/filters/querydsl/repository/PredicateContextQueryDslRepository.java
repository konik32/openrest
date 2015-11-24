package pl.openrest.filters.querydsl.repository;

import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import pl.openrest.filters.predicate.annotation.PredicateRepository;
import pl.openrest.filters.querydsl.query.QPredicateContext;
import pl.openrest.filters.repository.PredicateContextRepository;

/**
 * This repository has to be declared for every entity for which {@link PredicateRepository} has been declared.
 * 
 * @author Szymon Konicki
 *
 * @param <T>
 *            Entity type
 */
public interface PredicateContextQueryDslRepository<T> extends PredicateContextRepository<T, QPredicateContext>,
        QueryDslPredicateExecutor<T> {

}
