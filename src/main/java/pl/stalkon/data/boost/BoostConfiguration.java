package pl.stalkon.data.boost;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.data.geo.GeoModule;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.BoostMainController;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;
import org.springframework.data.rest.webmvc.json.Jackson2DatatypeHelper;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.RelProvider;
import org.springframework.hateoas.core.EvoInflectorRelProvider;
import org.springframework.hateoas.hal.CurieProvider;
import org.springframework.http.MediaType;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import pl.stalkon.data.boost.jpa.repository.BoostJpaRepository;
import pl.stalkon.data.boost.response.filter.BoostFilterProvider;
import pl.stalkon.data.boost.response.filter.RequestBasedFilterIntrospector;
import pl.stalkon.data.boost.response.filter.ContextFilterFactory;
import pl.stalkon.data.boost.webmvc.json.PersistentEntityWithAssociationsJackson2Module;
import pl.stalkon.data.rest.webmvc.ParsedRequestHandlerMethodArgumentResolver;
import pl.stalkon.data.rest.webmvc.ParsedRequestFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@Configuration
public class BoostConfiguration extends RepositoryRestMvcConfiguration {

	@Autowired
	private EntityLinks entityLinks;

	@Autowired
	private PagedResourcesAssembler<Object> assembler;

	@Autowired(required = false)
	RelProvider relProvider;
	@Autowired(required = false)
	CurieProvider curieProvider;

	@Bean
	public ParsedRequestHandlerMethodArgumentResolver partTreeSpecificationHandlerMethodArgumentResolver() {
		return new ParsedRequestHandlerMethodArgumentResolver(
				partTreeSpecificationFactory(),
				resourceMetadataHandlerMethodArgumentResolver());
	}

	@Override
	@Bean
	public RequestMappingHandlerAdapter repositoryExporterHandlerAdapter() {
		// TODO Auto-generated method stub
		RequestMappingHandlerAdapter adapter = super
				.repositoryExporterHandlerAdapter();
		List<HandlerMethodArgumentResolver> defaultArgumentResolvers = (List<HandlerMethodArgumentResolver>) PropertyAccessorFactory
				.forDirectFieldAccess(adapter).getPropertyValue(
						"argumentResolvers");

		List<HandlerMethodArgumentResolver> argumentResolvers = new ArrayList<HandlerMethodArgumentResolver>(
				defaultArgumentResolvers);

		argumentResolvers
				.add(partTreeSpecificationHandlerMethodArgumentResolver());

		PropertyAccessorFactory.forDirectFieldAccess(adapter).setPropertyValue(
				"argumentResolvers", argumentResolvers);
		return adapter;
	}

	@Bean
	public BoostMainController boostMainController() {
		return new BoostMainController(entityLinks, assembler,
				boostJpaRepository(), resourceMappings());
	}

	@Bean
	public BoostJpaRepository boostJpaRepository() {
		return new BoostJpaRepository();
	}

	@Bean
	public ParsedRequestFactory partTreeSpecificationFactory() {
		return new ParsedRequestFactory();
	}

	private RelProvider getDefaultedRelProvider() {
		return this.relProvider != null ? relProvider
				: new EvoInflectorRelProvider();
	}

	public PersistentEntityWithAssociationsJackson2Module persistentEntityJackson2Module() {
		return new PersistentEntityWithAssociationsJackson2Module(
				resourceMappings(), persistentEntities(), config(),
				uriToEntityConverter());
	}
	
	@Override
	public RepositoryRestConfiguration config() {
		// TODO Auto-generated method stub
		return super.config().setDefaultMediaType(MediaType.APPLICATION_JSON);
	}
	
	
	@Bean
	@Scope(value="request",proxyMode=ScopedProxyMode.TARGET_CLASS)
	public ContextFilterFactory spelMultiplePropertyFilterFactory(){
		return new ContextFilterFactory();
	}
	
	@Bean
	public BoostFilterProvider boostFilterProvider(){
		return new BoostFilterProvider();
	}

	@Override
	public ObjectMapper objectMapper() {
		// TODO Auto-generated method stub
		ObjectMapper mapper = basicObjectMapper();
		mapper.registerModule(persistentEntityJackson2Module());
		mapper.setFilters(boostFilterProvider());
		mapper.setAnnotationIntrospector(new RequestBasedFilterIntrospector());
		return mapper;
	}
	
	@Autowired
	GeoModule geoModule;

	private ObjectMapper basicObjectMapper() {

		ObjectMapper objectMapper = new ObjectMapper();

		objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,
				false);
		objectMapper.configure(
				DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		objectMapper.registerModule(geoModule);
		Jackson2DatatypeHelper.configureObjectMapper(objectMapper);
		// Configure custom Modules
		configureJacksonObjectMapper(objectMapper);

		return objectMapper;
	}

}
