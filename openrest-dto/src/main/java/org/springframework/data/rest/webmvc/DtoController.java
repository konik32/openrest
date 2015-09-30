package org.springframework.data.rest.webmvc;

import static org.springframework.http.HttpMethod.PATCH;
import static org.springframework.http.HttpMethod.PUT;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.mapping.model.BeanWrapper;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.core.event.AfterCreateEvent;
import org.springframework.data.rest.core.event.AfterSaveEvent;
import org.springframework.data.rest.core.event.BeforeCreateEvent;
import org.springframework.data.rest.core.event.BeforeSaveEvent;
import org.springframework.data.rest.core.invoke.RepositoryInvoker;
import org.springframework.data.rest.webmvc.ControllerUtils;
import org.springframework.data.rest.webmvc.PersistentEntityResource;
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.data.rest.webmvc.ResourceType;
import org.springframework.data.rest.webmvc.RootResourceInformation;
import org.springframework.data.rest.webmvc.support.BackendId;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.UriTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import pl.openrest.dto.event.AfterCreateWithDtoEvent;
import pl.openrest.dto.event.AfterSaveWithDtoEvent;
import pl.openrest.dto.event.BeforeCreateWithDtoEvent;
import pl.openrest.dto.event.BeforeSaveWithDtoEvent;
import pl.openrest.dto.webmvc.PersistentEntityResourceWithDtoWrapper;

@RepositoryRestController
public class DtoController implements ApplicationEventPublisherAware {

    private static final String BASE_MAPPING = "/{repository}";
    private final RepositoryRestConfiguration config;
    private final ConversionService defaultConversionService;

    private ApplicationEventPublisher publisher;

    @Autowired
    public DtoController(RepositoryRestConfiguration config, ConversionService defaultConversionService) {
        this.config = config;
        this.defaultConversionService = defaultConversionService;
    }

    @ResponseBody
    @RequestMapping(value = BASE_MAPPING, method = RequestMethod.POST, params = "dto")
    public ResponseEntity<ResourceSupport> postCollectionResource(RootResourceInformation resourceInformation,
            PersistentEntityResourceWithDtoWrapper resourceDtoWrapper, PersistentEntityResourceAssembler assembler)
            throws HttpRequestMethodNotSupportedException {

        resourceInformation.verifySupportedMethod(HttpMethod.POST, ResourceType.COLLECTION);

        return createAndReturn(resourceDtoWrapper.getEntityResource().getContent(), resourceDtoWrapper.getDto(),
                resourceInformation.getInvoker(), assembler);
    }

    @RequestMapping(value = BASE_MAPPING + "/{id}", method = RequestMethod.PUT, params = "dto")
    public ResponseEntity<? extends ResourceSupport> putItemResource(RootResourceInformation resourceInformation,
            PersistentEntityResourceWithDtoWrapper resourceDtoWrapper, @BackendId Serializable id,
            PersistentEntityResourceAssembler assembler) throws HttpRequestMethodNotSupportedException {

        resourceInformation.verifySupportedMethod(HttpMethod.PUT, ResourceType.ITEM);

        // Force ID on unmarshalled object
        BeanWrapper<Object> incomingWrapper = BeanWrapper.create(resourceDtoWrapper.getEntityResource().getContent(),
                defaultConversionService);
        incomingWrapper.setProperty(resourceDtoWrapper.getEntityResource().getPersistentEntity().getIdProperty(), id);

        RepositoryInvoker invoker = resourceInformation.getInvoker();
        Object objectToSave = incomingWrapper.getBean();

        return invoker.invokeFindOne(id) == null ? createAndReturn(objectToSave, resourceDtoWrapper.getDto(), invoker, assembler)
                : saveAndReturn(objectToSave, resourceDtoWrapper.getDto(), invoker, PUT, assembler);
    }

    @RequestMapping(value = BASE_MAPPING + "/{id}", method = RequestMethod.PATCH, params = "dto")
    public ResponseEntity<ResourceSupport> patchItemResource(RootResourceInformation resourceInformation,
            PersistentEntityResourceWithDtoWrapper resourceDtoWrapper, @BackendId Serializable id,
            PersistentEntityResourceAssembler assembler) throws HttpRequestMethodNotSupportedException, ResourceNotFoundException {

        resourceInformation.verifySupportedMethod(HttpMethod.PATCH, ResourceType.ITEM);

        return saveAndReturn(resourceDtoWrapper.getEntityResource().getContent(), resourceDtoWrapper.getDto(),
                resourceInformation.getInvoker(), PATCH, assembler);
    }

    /**
     * Merges the given incoming object into the given domain object.
     * 
     * @param incoming
     * @param domainObject
     * @param invoker
     * @param httpMethod
     * @return
     */
    @Transactional
    private ResponseEntity<ResourceSupport> saveAndReturn(Object domainObject, Object dto, RepositoryInvoker invoker,
            HttpMethod httpMethod, PersistentEntityResourceAssembler assembler) {
        publisher.publishEvent(new BeforeSaveEvent(domainObject));
        publisher.publishEvent(new BeforeSaveWithDtoEvent(domainObject, dto));
        Object obj = invoker.invokeSave(domainObject);
        publisher.publishEvent(new AfterSaveEvent(domainObject));
        publisher.publishEvent(new AfterSaveWithDtoEvent(domainObject, dto));

        HttpHeaders headers = new HttpHeaders();

        if (PUT.equals(httpMethod)) {
            addLocationHeader(headers, assembler, obj);
        }

        if (config.isReturnBodyOnUpdate()) {
            return ControllerUtils.toResponseEntity(HttpStatus.OK, headers, assembler.toFullResource(obj));
        } else {
            return ControllerUtils.toEmptyResponse(HttpStatus.NO_CONTENT, headers);
        }
    }

    /**
     * Triggers the creation of the domain object and renders it into the response if needed.
     * 
     * @param domainObject
     * @param invoker
     * @return
     */
    @Transactional
    private ResponseEntity<ResourceSupport> createAndReturn(Object domainObject, Object dto, RepositoryInvoker invoker,
            PersistentEntityResourceAssembler assembler) {
        publisher.publishEvent(new BeforeCreateEvent(domainObject));
        publisher.publishEvent(new BeforeCreateWithDtoEvent(domainObject, dto));
        Object savedObject = invoker.invokeSave(domainObject);
        publisher.publishEvent(new AfterCreateEvent(domainObject));
        publisher.publishEvent(new AfterCreateWithDtoEvent(savedObject, dto));

        HttpHeaders headers = new HttpHeaders();
        addLocationHeader(headers, assembler, savedObject);

        PersistentEntityResource resource = config.isReturnBodyOnCreate() ? assembler.toFullResource(savedObject) : null;
        return ControllerUtils.toResponseEntity(HttpStatus.CREATED, headers, resource);
    }

    /**
     * Sets the location header pointing to the resource representing the given instance. Will make sure we properly expand the URI template
     * potentially created as self link.
     * 
     * @param headers
     *            must not be {@literal null}.
     * @param assembler
     *            must not be {@literal null}.
     * @param source
     *            must not be {@literal null}.
     */
    private void addLocationHeader(HttpHeaders headers, PersistentEntityResourceAssembler assembler, Object source) {

        String selfLink = assembler.getSelfLinkFor(source).getHref();
        headers.setLocation(new UriTemplate(selfLink).expand());
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }
}
