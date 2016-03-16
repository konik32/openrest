package pl.openrest.dto.repository;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Map;

import lombok.NonNull;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.support.RepositoryInvoker;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;

import pl.openrest.dto.annotations.Dto;
import pl.openrest.dto.event.AfterCreateWithDtoEvent;
import pl.openrest.dto.event.AfterSaveWithDtoEvent;
import pl.openrest.dto.event.BeforeCreateWithDtoEvent;
import pl.openrest.dto.event.BeforeSaveWithDtoEvent;
import pl.openrest.dto.handler.DtoRequestContext;

public class DtoRepositoryInvokerAdapter implements RepositoryInvoker, ApplicationEventPublisherAware {
    private final RepositoryInvoker delegate;

    private DtoRequestContext dtoRequestContext;
    private ApplicationEventPublisher publisher;

    public DtoRepositoryInvokerAdapter(@NonNull RepositoryInvoker delegate) {
        this.delegate = delegate;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.data.repository.support.RepositoryInvoker#invokeSave (java.lang.Object)
     */
    @Override
    @Transactional
    public <T> T invokeSave(T object) {
        if (dtoRequestContext.isNew())
            publisher.publishEvent(new BeforeCreateWithDtoEvent(object, dtoRequestContext.getDto()));
        else
            publisher.publishEvent(new BeforeSaveWithDtoEvent(object, dtoRequestContext.getDto()));
        T result = delegate.invokeSave(object);
        if (dtoRequestContext.isNew())
            publisher.publishEvent(new AfterCreateWithDtoEvent(object, dtoRequestContext.getDto()));
        else
            publisher.publishEvent(new AfterSaveWithDtoEvent(object, dtoRequestContext.getDto()));
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.data.repository.support.RepositoryInvoker#invokeFindOne (java.io.Serializable)
     */
    @Override
    public Object invokeFindOne(Serializable id) {
        return delegate.invokeFindOne(id);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.data.repository.support.RepositoryInvoker# invokeQueryMethod(java.lang.reflect.Method, java.util.Map,
     * org.springframework.data.domain.Pageable, org.springframework.data.domain.Sort)
     */
    @Override
    public Object invokeQueryMethod(Method method, Map<String, String[]> parameters, Pageable pageable, Sort sort) {
        return delegate.invokeQueryMethod(method, parameters, pageable, sort);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.data.repository.support.RepositoryInvoker# invokeQueryMethod(java.lang.reflect.Method,
     * org.springframework.util.MultiValueMap, org.springframework.data.domain.Pageable, org.springframework.data.domain.Sort)
     */
    @Override
    public Object invokeQueryMethod(Method method, MultiValueMap<String, ? extends Object> parameters, Pageable pageable, Sort sort) {
        return delegate.invokeQueryMethod(method, parameters, pageable, sort);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.data.repository.support.RepositoryInvoker#invokeFindAll (org.springframework.data.domain.Pageable)
     */
    @Override
    public Iterable<Object> invokeFindAll(Pageable pageable) {
        return delegate.invokeFindAll(pageable);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.data.repository.support.RepositoryInvoker#invokeFindAll (org.springframework.data.domain.Sort)
     */
    @Override
    public Iterable<Object> invokeFindAll(Sort sort) {
        return delegate.invokeFindAll(sort);
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

    public void setDtoRequestContext(DtoRequestContext dtoRequestContext) {
        this.dtoRequestContext = dtoRequestContext;
    }
}
