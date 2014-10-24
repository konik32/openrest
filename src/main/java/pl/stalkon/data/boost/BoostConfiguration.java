package pl.stalkon.data.boost;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.data.geo.GeoModule;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.core.projection.ProxyProjectionFactory;
import org.springframework.data.rest.webmvc.BoostMainController;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;
import org.springframework.data.rest.webmvc.json.Jackson2DatatypeHelper;
import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.RelProvider;
import org.springframework.hateoas.core.EvoInflectorRelProvider;
import org.springframework.hateoas.hal.CurieProvider;
import org.springframework.hateoas.hal.Jackson2HalModule;
import org.springframework.hateoas.hal.Jackson2HalModule.HalHandlerInstantiator;
import org.springframework.http.MediaType;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import pl.stalkon.data.boost.jpa.repository.BoostJpaRepository;
import pl.stalkon.data.boost.response.filter.BoostFilterProvider;
import pl.stalkon.data.boost.response.filter.RequestBasedFilterIntrospector;
import pl.stalkon.data.boost.response.filter.ContextFilterFactory;
import pl.stalkon.data.boost.response.filter.SpelMultiplePropertyFilter;
import pl.stalkon.data.boost.webmvc.json.PersistentEntityWithAssociationsJackson2Module;
import pl.stalkon.data.rest.webmvc.BoostPersistentEntityResourceAssembler;
import pl.stalkon.data.rest.webmvc.BoostPersistentEntityResourceAssemblerArgumentResolver;
import pl.stalkon.data.rest.webmvc.ParsedRequestHandlerMethodArgumentResolver;
import pl.stalkon.data.rest.webmvc.ParsedRequestFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@Configuration
public class BoostConfiguration extends RepositoryRestMvcConfiguration {

	@Autowired
	private RepositoryEntityLinks entityLinks;

	@Autowired
	ListableBeanFactory beanFactory;

	@Autowired
	private PagedResourcesAssembler<Object> assembler;

	@Autowired(required = false)
	RelProvider relProvider;
	@Autowired(required = false)
	CurieProvider curieProvider;

	@Bean
	public ParsedRequestHandlerMethodArgumentResolver partTreeSpecificationHandlerMethodArgumentResolver() {
		return new ParsedRequestHandlerMethodArgumentResolver(partTreeSpecificationFactory(), resourceMetadataHandlerMethodArgumentResolver());
	}

	@Bean
	public BoostPersistentEntityResourceAssemblerArgumentResolver boostPersistentEntityResourceAssemblerArgumentResolver() {
		return new BoostPersistentEntityResourceAssemblerArgumentResolver(repositories(), entityLinks(), config().projectionConfiguration(),
				new ProxyProjectionFactory(beanFactory), resourceMappings());
	}

	@Override
	@Bean
	public RequestMappingHandlerAdapter repositoryExporterHandlerAdapter() {
		// TODO Auto-generated method stub
		RequestMappingHandlerAdapter adapter = super.repositoryExporterHandlerAdapter();
		List<HandlerMethodArgumentResolver> defaultArgumentResolvers = (List<HandlerMethodArgumentResolver>) PropertyAccessorFactory.forDirectFieldAccess(
				adapter).getPropertyValue("argumentResolvers");

		List<HandlerMethodArgumentResolver> argumentResolvers = new ArrayList<HandlerMethodArgumentResolver>(defaultArgumentResolvers);

		argumentResolvers.add(partTreeSpecificationHandlerMethodArgumentResolver());
		argumentResolvers.add(boostPersistentEntityResourceAssemblerArgumentResolver());

		PropertyAccessorFactory.forDirectFieldAccess(adapter).setPropertyValue("argumentResolvers", argumentResolvers);
		return adapter;
	}

	@Bean
	public BoostMainController boostMainController() {
		return new BoostMainController(entityLinks, assembler, boostJpaRepository(), resourceMappings());
	}

	@Bean
	public BoostJpaRepository boostJpaRepository() {
		return new BoostJpaRepository();
	}

	@Bean
	public ParsedRequestFactory partTreeSpecificationFactory() {
		return new ParsedRequestFactory();
	}

	@Bean
	@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
	public ContextFilterFactory contextFilterFactory() {
		return new ContextFilterFactory();
	}

	@Bean
	public BoostFilterProvider boostFilterProvider() {
		BoostFilterProvider provider = new BoostFilterProvider();
		provider.addContextFilter(SpelMultiplePropertyFilter.FILTER_ID, SpelMultiplePropertyFilter.class);
		return provider;
	}

	@Override
	public ObjectMapper objectMapper() {
		ObjectMapper mapper = super.objectMapper();
		mapper.setFilters(boostFilterProvider());
		mapper.setAnnotationIntrospector(new RequestBasedFilterIntrospector());
		return mapper;
	}

	@Bean
	public ObjectMapper halObjectMapper() {
		ObjectMapper mapper = super.halObjectMapper();
		mapper.setFilters(boostFilterProvider());
		mapper.setAnnotationIntrospector(new RequestBasedFilterIntrospector());
		return mapper;
	}

}
