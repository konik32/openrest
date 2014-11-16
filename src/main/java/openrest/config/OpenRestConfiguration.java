package openrest.config;

import java.util.ArrayList;
import java.util.List;

import openrest.jpa.repository.PartTreeSpecificationRepositoryImpl;
import openrest.query.StaticFilterFactory;
import openrest.response.filter.ContextFilterFactory;
import openrest.response.filter.ContextFilterProvider;
import openrest.response.filter.RequestBasedFilterIntrospector;
import openrest.response.filter.SpelMultiplePropertyFilter;
import openrest.webmvc.ParsedRequestFactory;
import openrest.webmvc.ParsedRequestHandlerMethodArgumentResolver;
import openrest.webmvc.PersistentEntityWithAssociationsResourceAssemblerArgumentResolver;
import openrest.webmvc.support.OpenRestEntityLinks;

import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.mapping.context.PersistentEntities;
import org.springframework.data.rest.core.projection.ProxyProjectionFactory;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;
import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class OpenRestConfiguration extends RepositoryRestMvcConfiguration {

	@Autowired
	private ListableBeanFactory beanFactory;

	@Autowired
	private PagedResourcesAssembler<Object> assembler;
	
	@Autowired
	private PersistentEntities persistentEntities;
	
	@Bean
	public ParsedRequestHandlerMethodArgumentResolver partTreeSpecificationHandlerMethodArgumentResolver() {
		return new ParsedRequestHandlerMethodArgumentResolver(partTreeSpecificationFactory(), resourceMetadataHandlerMethodArgumentResolver(), config());
	}

	@Bean
	public PersistentEntityWithAssociationsResourceAssemblerArgumentResolver boostPersistentEntityResourceAssemblerArgumentResolver() {
		return new PersistentEntityWithAssociationsResourceAssemblerArgumentResolver(repositories(), entityLinks(), config().projectionConfiguration(),
				new ProxyProjectionFactory(beanFactory), resourceMappings());
	}

	@Bean
	public StaticFilterFactory staticFilterFactory(){
		return new StaticFilterFactory();
	}
	
	@Bean
	public OpenRestEntityLinks entityLinks() {
		return new OpenRestEntityLinks(repositories(), resourceMappings(), config(), pageableResolver(),
				backendIdConverterRegistry(), partTreeSpecificationHandlerMethodArgumentResolver());
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
	
//	@Bean
//	public OpenRestMainController boostMainController() {
//		return new OpenRestMainController(repositories(), config(),entityLinks(), assembler, conversionService, boostJpaRepository(), resourceMappings());
//	}

	@Bean
	public PartTreeSpecificationRepositoryImpl boostJpaRepository() {
		return new PartTreeSpecificationRepositoryImpl();
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
	public ContextFilterProvider boostFilterProvider() {
		ContextFilterProvider provider = new ContextFilterProvider();
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
