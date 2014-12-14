package openrest.webmvc;

import openrest.dto.DtoPopulatorEvent;
import openrest.dto.DtoPopulatorInvoker;
import openrest.httpquery.parser.Parsers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.MethodParameter;
import org.springframework.data.repository.support.Repositories;
import org.springframework.data.rest.core.mapping.ResourceMappings;
import org.springframework.data.rest.core.projection.ProjectionDefinitions;
import org.springframework.data.rest.core.projection.ProjectionFactory;
import org.springframework.data.rest.webmvc.config.PersistentEntityResourceAssemblerArgumentResolver;
import org.springframework.data.rest.webmvc.support.PersistentEntityProjector;
import org.springframework.hateoas.EntityLinks;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * Modification of {@link PersistentEntityResourceAssemblerArgumentResolver} to
 * return {@link PersistentEntityWithAssociationsResourceAssembler}
 * 
 * @author Szymon Konicki
 *
 */
@Component
public class PersistentEntityWithAssociationsResourceAssemblerArgumentResolver implements HandlerMethodArgumentResolver {

	private final Repositories repositories;
	private final EntityLinks entityLinks;
	private final ProjectionDefinitions projectionDefinitions;
	private final ProjectionFactory projectionFactory;
	private final ResourceMappings mappings;
	private final ApplicationEventPublisher publisher;

	@Autowired
	public PersistentEntityWithAssociationsResourceAssemblerArgumentResolver(Repositories repositories, EntityLinks entityLinks,
			ProjectionDefinitions projectionDefinitions, ProjectionFactory projectionFactory, ResourceMappings mappings, ApplicationEventPublisher publisher) {
		Assert.notNull(repositories, "Repositories must not be null!");
		Assert.notNull(entityLinks, "EntityLinks must not be null!");
		Assert.notNull(projectionDefinitions, "ProjectionDefinitions must not be null!");
		Assert.notNull(projectionFactory, "ProjectionFactory must not be null!");
		Assert.notNull(projectionFactory, "DtoPopulatorInvoker must not be null!");

		this.repositories = repositories;
		this.entityLinks = entityLinks;
		this.projectionDefinitions = projectionDefinitions;
		this.projectionFactory = projectionFactory;
		this.mappings = mappings;
		this.publisher = publisher;
	}

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return PersistentEntityWithAssociationsResourceAssembler.class.equals(parameter.getParameterType());
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory)
			throws Exception {
		return getResourceAssembler(webRequest);
	}

	public PersistentEntityWithAssociationsResourceAssembler getResourceAssembler(NativeWebRequest webRequest) {
		String projectionParameter = webRequest.getParameter(projectionDefinitions.getParameterName());
		String dtos = webRequest.getParameter(ParsedRequestHandlerMethodArgumentResolver.DTO_PARAM_NAME);
		PersistentEntityProjector projector = new PersistentEntityProjector(projectionDefinitions, projectionFactory, projectionParameter, mappings);
		return new PersistentEntityWithAssociationsResourceAssembler(repositories, entityLinks, projector, mappings, publisher, Parsers.parseDtos(dtos));
	}

}
