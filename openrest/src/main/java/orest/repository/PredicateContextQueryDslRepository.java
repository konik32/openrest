package orest.repository;

import orest.expression.registry.ExpressionRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import com.mysema.query.types.OrderSpecifier;
import com.mysema.query.types.Predicate;
/**
 * This repository has to be declared for every entity for which {@link ExpressionRepository} has been declared.
 * @author Szymon Konicki
 *
 * @param <T> Entity type
 */
public interface PredicateContextQueryDslRepository<T> extends QueryDslPredicateExecutor<T> {
	
	T findOne(Predicate predicate, PredicateContext predicateContext);
	Iterable<T> findAll(Predicate predicate, PredicateContext predicateContext);
	Iterable<T> findAll(Predicate predicate, PredicateContext predicateContext, OrderSpecifier<?>... orders);
	Page<T> findAll(Predicate predicate, Pageable pageable, PredicateContext predicateContext);
	long count(Predicate predicate, PredicateContext predicateContext);
	
}
