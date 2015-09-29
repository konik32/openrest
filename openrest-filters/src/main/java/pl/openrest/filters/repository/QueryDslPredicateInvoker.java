package pl.openrest.filters.repository;

import lombok.Data;

import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.QSort;

import pl.openrest.filters.query.PredicateContext;

import com.mysema.query.types.Predicate;
import com.mysema.query.types.path.PathBuilder;

@Data
public class QueryDslPredicateInvoker {
    private final PredicateContextQueryDslRepository<Object> repository;
    private final PathBuilder<?> builder;

    public Iterable<Object> invokeFindAll(PredicateContext predicateContext, Pageable pageable) {
        return pageable == null ? invokeFindAll(predicateContext) : repository.findAll(predicateContext, pageable);
    }

    public Iterable<Object> invokeFindAll(PredicateContext predicateContext, QSort sort) {
        if (sort == null)
            return invokeFindAll(predicateContext);
        return repository.findAll(predicateContext, sort);
    }

    public Iterable<Object> invokeFindAll(PredicateContext predicateContext) {
        return repository.findAll(predicateContext);
    }

    public Object invokeFindOne(Predicate predicate, PredicateContext predicateContext) {
        return repository.findOne(predicateContext);
    }

    public Object invokeCount(Predicate predicate, PredicateContext predicateContext) {
        return repository.count(predicateContext);
    }
}
