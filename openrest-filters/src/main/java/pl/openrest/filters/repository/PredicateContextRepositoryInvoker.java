package pl.openrest.filters.repository;

import lombok.NonNull;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import pl.openrest.filters.query.PredicateContext;

public class PredicateContextRepositoryInvoker {
    private final PredicateContextRepository<Object, PredicateContext<?>> repository;

    public PredicateContextRepositoryInvoker(@NonNull PredicateContextRepository<Object, PredicateContext<?>> repository) {
        this.repository = repository;
    }

    public Iterable<Object> invokeFindAll(PredicateContext<?> predicateContext, Pageable pageable) {
        return pageable == null ? invokeFindAll(predicateContext) : repository.findAll(predicateContext, pageable);
    }

    public Iterable<Object> invokeFindAll(PredicateContext<?> predicateContext, Sort sort) {
        if (sort == null)
            return invokeFindAll(predicateContext);
        return repository.findAll(predicateContext, sort);
    }

    public Iterable<Object> invokeFindAll(PredicateContext<?> predicateContext) {
        return repository.findAll(predicateContext);
    }

    public Object invokeFindOne(PredicateContext<?> predicateContext) {
        return repository.findOne(predicateContext);
    }

    public Object invokeCount(PredicateContext<?> predicateContext) {
        return repository.count(predicateContext);
    }
}
