package pl.openrest.filters.repository;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Map;

import lombok.NonNull;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.support.RepositoryInvoker;
import org.springframework.util.MultiValueMap;

import pl.openrest.filters.query.PredicateContext;
import pl.openrest.filters.webmvc.DefaultedPageRequest;

public class PredicateContextRepositoryInvokerAdapter implements RepositoryInvoker {
    private final PredicateContextRepository<Object, PredicateContext<?>> executor;
    private final PredicateContext<?> predicateContext;
    private final RepositoryInvoker delegate;
    private boolean addDefaultPageable = true;

    public PredicateContextRepositoryInvokerAdapter(@NonNull PredicateContextRepository<Object, PredicateContext<?>> executor,
            @NonNull PredicateContext<?> predicateContext, @NonNull RepositoryInvoker delegate) {
        this.executor = executor;
        this.predicateContext = predicateContext;
        this.delegate = delegate;
    }

    public Object invokeFindOne() {
        return executor.findOne(predicateContext);
    }

    public Object invokeCount(PredicateContext<?> predicateContext) {
        return executor.count(predicateContext);
    }

    private boolean checkIfAddPageable(Pageable pageable) {
        if (!DefaultedPageRequest.class.isAssignableFrom(pageable.getClass()))
            return true;
        return !((DefaultedPageRequest) pageable).isDefault() || addDefaultPageable;
    }

    private Object getResult(PredicateContext<?> predicateContext, Pageable pageable, Sort sort, boolean addDefaultPageable) {
        Iterable<Object> result;
        if (checkIfAddPageable(pageable))
            result = executor.findAll(predicateContext, pageable);
        else
            result = executor.findAll(predicateContext, sort);
        return result;
    }

    public Object invokeCountQueryMethod() {
        return executor.count(predicateContext);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.data.repository.support.RepositoryInvoker#invokeFindOne (java.io.Serializable)
     */
    @Override
    public Object invokeFindOne(Serializable id) {
        return executor.findOne(predicateContext);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.data.repository.support.RepositoryInvoker# invokeQueryMethod(java.lang.reflect.Method, java.util.Map,
     * org.springframework.data.domain.Pageable, org.springframework.data.domain.Sort)
     */
    @Override
    public Object invokeQueryMethod(Method method, Map<String, String[]> parameters, Pageable pageable, Sort sort) {
        return getResult(predicateContext, pageable, sort, addDefaultPageable);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.data.repository.support.RepositoryInvoker# invokeQueryMethod(java.lang.reflect.Method,
     * org.springframework.util.MultiValueMap, org.springframework.data.domain.Pageable, org.springframework.data.domain.Sort)
     */
    @Override
    public Object invokeQueryMethod(Method method, MultiValueMap<String, ? extends Object> parameters, Pageable pageable, Sort sort) {
        return getResult(predicateContext, pageable, sort, addDefaultPageable);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.data.repository.support.RepositoryInvoker#invokeFindAll (org.springframework.data.domain.Pageable)
     */
    @Override
    public Iterable<Object> invokeFindAll(Pageable pageable) {
        return (Iterable<Object>) getResult(predicateContext, pageable, null, addDefaultPageable);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.data.repository.support.RepositoryInvoker#invokeFindAll (org.springframework.data.domain.Sort)
     */
    @Override
    public Iterable<Object> invokeFindAll(Sort sort) {
        return executor.findAll(predicateContext, sort);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.data.repository.support.RepositoryInvocationInformation #hasDeleteMethod()
     */
    @Override
    public boolean hasDeleteMethod() {
        return delegate.hasDeleteMethod();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.data.repository.support.RepositoryInvocationInformation #hasFindAllMethod()
     */
    @Override
    public boolean hasFindAllMethod() {
        return delegate.hasFindAllMethod();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.data.repository.support.RepositoryInvocationInformation #hasFindOneMethod()
     */
    @Override
    public boolean hasFindOneMethod() {
        return delegate.hasFindOneMethod();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.data.repository.support.RepositoryInvocationInformation #hasSaveMethod()
     */
    @Override
    public boolean hasSaveMethod() {
        return delegate.hasSaveMethod();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.data.repository.support.RepositoryInvoker#invokeDelete (java.io.Serializable)
     */
    @Override
    public void invokeDelete(Serializable id) {
        delegate.invokeDelete(id);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.data.repository.support.RepositoryInvoker#invokeSave (java.lang.Object)
     */
    @Override
    public <T> T invokeSave(T object) {
        return delegate.invokeSave(object);
    }

    public void setAddDefaultPageable(boolean addDefaultPageable) {
        this.addDefaultPageable = addDefaultPageable;
    }
}
