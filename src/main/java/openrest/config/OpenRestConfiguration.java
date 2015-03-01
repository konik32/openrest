package openrest.config;

import java.util.ArrayList;
import java.util.List;

import openrest.dto.DtoPopulatorEvent;
import openrest.dto.DtoPopulatorInvoker;
import openrest.dto.EmbeddedWrapperFactory;
import openrest.event.AnnotatedEventHandlerBeanPostProcessor;
import openrest.jpa.repository.OpenRestRepositoryImpl;
import openrest.query.filter.StaticFilterFactory;
import openrest.security.resource.filter.ResourceFilterRegister;
import openrest.webmvc.ParsedRequestFactory;
import openrest.webmvc.ParsedRequestHandlerMethodArgumentResolver;
import openrest.webmvc.PersistentEntityWithAssociationsResourceAssemblerArgumentResolver;
import openrest.webmvc.support.OpenRestEntityLinks;

import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.data.mapping.context.PersistentEntities;
import org.springframework.data.rest.core.projection.ProxyProjectionFactory;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.ser.FilterProvider;

@Configuration
public class OpenRestConfiguration extends RepositoryRestMvcConfiguration {

	@Autowired
	private ListableBeanFactory beanFactory;

	@Autowired
	private PagedResourcesAssembler<Object> assembler;

	@Autowired
	private PersistentEntities persistentEntities;

	@Autowired
	private ApplicationEventPublisher publisher;

	@Bean
	public ParsedRequestHandlerMethodArgumentResolver partTreeSpecificationHandlerMethodArgumentResolver() {
		return new ParsedRequestHandlerMethodArgumentResolver(partTreeSpecificationFactory(), resourceMetadataHandlerMethodArgumentResolver(),
				pageableResolver(), config());
	}

	@Bean
	public static DtoPopulatorInvoker dtoPopulatorInvoker() {
		return new DtoPopulatorInvoker();
	}

	@Bean
	@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
	public EmbeddedWrapperFactory embeddedWrapperFactory() {
		return new EmbeddedWrapperFactory(boostPersistentEntityResourceAssemblerArgumentResolver());
	}

	@Bean
	public PersistentEntityWithAssociationsResourceAssemblerArgumentResolver boostPersistentEntityResourceAssemblerArgumentResolver() {
		return new PersistentEntityWithAssociationsResourceAssemblerArgumentResolver(repositories(), entityLinks(), config().projectionConfiguration(),
				new ProxyProjectionFactory(beanFactory), resourceMappings(), publisher);
	}

	@Bean
	@Autowired
	public StaticFilterFactory filterFactory(PersistentEntities persistentEntities) {
		StaticFilterFactory factory = new StaticFilterFactory();
		factory.setPersistentEntities(persistentEntities);
		return factory;
	}

	@Bean
	public OpenRestEntityLinks entityLinks() {
		return new OpenRestEntityLinks(repositories(), resourceMappings(), config(), pageableResolver(), backendIdConverterRegistry(),
				partTreeSpecificationHandlerMethodArgumentResolver());
	}

	@Bean
	public static AnnotatedEventHandlerBeanPostProcessor annotatedEventHandlerBeanPostProcessor() {
		return new AnnotatedEventHandlerBeanPostProcessor();
	}
	
	@Bean
	public static ResourceFilterRegister ResourceFilterRegister() {
		return new ResourceFilterRegister();
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

	// @Bean
	// public OpenRestMainController boostMainController() {
	// return new OpenRestMainController(repositories(), config(),entityLinks(),
	// assembler, conversionService, boostJpaRepository(), resourceMappings());
	// }

	@Bean
	public OpenRestRepositoryImpl boostJpaRepository() {
		return new OpenRestRepositoryImpl();
	}

	@Bean
	public ParsedRequestFactory partTreeSpecificationFactory() {
		return new ParsedRequestFactory();
	}

	@Override
	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper mapper = super.objectMapper();
		return mapper;
	}

	@Bean
	public ObjectMapper halObjectMapper() {
		ObjectMapper mapper = super.halObjectMapper();
		return mapper;
	}

}
