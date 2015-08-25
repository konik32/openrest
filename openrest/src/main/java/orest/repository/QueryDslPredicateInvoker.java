package orest.repository;

import lombok.Data;

import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.QSort;

import com.mysema.query.types.Predicate;
import com.mysema.query.types.path.PathBuilder;

@Data
public class QueryDslPredicateInvoker {
	private final PredicateContextQueryDslRepository<Object> repository;
	private final PathBuilder<?> builder;

	public Iterable<Object> invokeFindAll(Predicate predicate, PredicateContext predicateContext, Pageable pageable) {
		return pageable == null ? invokeFindAll(predicate, predicateContext) : repository.findAll(predicate,
				predicateContext, pageable);
	}

	public Iterable<Object> invokeFindAll(Predicate predicate, PredicateContext predicateContext, QSort sort) {
		if (sort == null)
			return invokeFindAll(predicate, predicateContext);
		return repository.findAll(predicate, predicateContext, sort);
	}

	public Iterable<Object> invokeFindAll(Predicate predicate, PredicateContext predicateContext) {
		return repository.findAll(predicate, predicateContext);
	}
	
	public Object invokeFindOne(Predicate predicate, PredicateContext predicateContext){
	    return repository.findOne(predicate, predicateContext);
	}

}
