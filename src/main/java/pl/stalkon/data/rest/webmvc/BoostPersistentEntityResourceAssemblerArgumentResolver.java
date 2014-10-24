package pl.stalkon.data.rest.webmvc;

import org.springframework.core.MethodParameter;
import org.springframework.data.repository.support.Repositories;
import org.springframework.data.rest.core.mapping.ResourceMappings;
import org.springframework.data.rest.core.projection.ProjectionDefinitions;
import org.springframework.data.rest.core.projection.ProjectionFactory;
import org.springframework.data.rest.webmvc.support.PersistentEntityProjector;
import org.springframework.hateoas.EntityLinks;
import org.springframework.util.Assert;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class BoostPersistentEntityResourceAssemblerArgumentResolver implements HandlerMethodArgumentResolver  {

	private final Repositories repositories;
	private final EntityLinks entityLinks;
	private final ProjectionDefinitions projectionDefinitions;
	private final ProjectionFactory projectionFactory;
	private final ResourceMappings mappings;
	
	public BoostPersistentEntityResourceAssemblerArgumentResolver(Repositories repositories, EntityLinks entityLinks,
			ProjectionDefinitions projectionDefinitions, ProjectionFactory projectionFactory, ResourceMappings mappings) {
		Assert.notNull(repositories, "Repositories must not be null!");
		Assert.notNull(entityLinks, "EntityLinks must not be null!");
		Assert.notNull(projectionDefinitions, "ProjectionDefinitions must not be null!");
		Assert.notNull(projectionFactory, "ProjectionFactory must not be null!");

		this.repositories = repositories;
		this.entityLinks = entityLinks;
		this.projectionDefinitions = projectionDefinitions;
		this.projectionFactory = projectionFactory;
		this.mappings = mappings;
	}

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return BoostPersistentEntityResourceAssembler.class.equals(parameter.getParameterType());
	}
	
	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		String projectionParameter = webRequest.getParameter(projectionDefinitions.getParameterName());
		PersistentEntityProjector projector = new PersistentEntityProjector(projectionDefinitions, projectionFactory,
				projectionParameter, mappings);
		return new BoostPersistentEntityResourceAssembler(repositories, entityLinks, projector, mappings);
	}

}
