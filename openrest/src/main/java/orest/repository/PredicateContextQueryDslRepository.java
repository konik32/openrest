package orest.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import com.mysema.query.types.OrderSpecifier;
import com.mysema.query.types.Predicate;

public interface PredicateContextQueryDslRepository<T> extends QueryDslPredicateExecutor<T> {
	
	T findOne(Predicate predicate, PredicateContext predicateContext);
	Iterable<T> findAll(Predicate predicate, PredicateContext predicateContext);
	Iterable<T> findAll(Predicate predicate, PredicateContext predicateContext, OrderSpecifier<?>... orders);
	Page<T> findAll(Predicate predicate, Pageable pageable, PredicateContext predicateContext);
	long count(Predicate predicate, PredicateContext predicateContext);
	
}
