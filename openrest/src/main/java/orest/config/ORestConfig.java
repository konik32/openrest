package orest.config;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.Validator;

import orest.dto.DefaultEntityFromDtoCreator;
import orest.dto.Dto;
import orest.dto.DtoDomainRegistry;
import orest.dto.DtoInformation;
import orest.expression.ExpressionBuilder;
import orest.expression.SpelEvaluatorBean;
import orest.expression.registry.EntityExpressionMethodsRegistry;
import orest.expression.registry.Expand;
import orest.expression.registry.ExpressionMethodInformation.Join;
import orest.expression.registry.ProjectionInfo;
import orest.expression.registry.ProjectionInfoRegistry;
import orest.json.DtoAwareDeserializerModifier;
import orest.mvc.NonOrestRequestsInterceptor;
import orest.parser.FilterStringParser;
import orest.security.ExpressionEvaluator;
import orest.security.RequestScopedExpressionEvaluator;
import orest.security.Secure;
import orest.security.SecurityExpressionContextHolder;
import orest.security.SecurityExpressionContextHolderImpl;
import orest.security.SimpleSecurityExpressionHandler;
import orest.validation.UpdateValidationContext;
import orest.webmvc.support.OpenRestEntityLinks;

import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.mapping.context.PersistentEntities;
import org.springframework.data.repository.support.Repositories;
import org.springframework.data.rest.core.UriToEntityConverter;
import org.springframework.data.rest.core.config.Projection;
import org.springframework.data.rest.webmvc.config.DtoAwarePersistentEntityResourceHandlerMethodArgumentResolver;
import org.springframework.data.rest.webmvc.config.PersistentEntityResourceHandlerMethodArgumentResolver;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;
import org.springframework.data.rest.webmvc.json.DomainObjectReader;
import org.springframework.data.rest.webmvc.mapping.AssociationLinks;
import org.springframework.data.util.AnnotatedTypeScanner;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.util.Assert;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.mysema.query.types.path.PathBuilder;
import com.mysema.query.types.path.PathBuilderFactory;

/**
 * 
 * Main application configuration for OPEN REST. Import it to your application's
 * main configuration class and add {@link
 * @EnableJpaRepositories(repositoryFactoryBeanClass =
 * ExpressionJpaFactoryBean.class)}.
 * 
 * @author Szymon Konicki
 *
 */
@Configuration
public class ORestConfig extends RepositoryRestMvcConfiguration {
	@Autowired
	private Repositories repositories;
	@Autowired
	private ConversionService defaultConversionService;
	@Autowired
	private ListableBeanFactory beanFactory;

	@Autowired(required = false)
	private List<PermissionEvaluator> permissionEvaluators;
	@Autowired
	private ApplicationContext applicationContext;
	@Autowired(required = false)
	private RoleHierarchy roleHierarchy;
	@Autowired
	private PersistentEntities persistentEntities;

	@Autowired
	private DtoDomainRegistry dtoDomainRegistry;

	@Autowired(required = false)
	private Validator validator;

	@Bean
	public EntityExpressionMethodsRegistry entityExpressionMethodsRegistry() {
		return new EntityExpressionMethodsRegistry(beanFactory, repositories, persistentEntities);
	}

	@Bean
	@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
	public UpdateValidationContext getUpdateValidationContext() {
		return new UpdateValidationContext();
	}

	@Bean
	public SimpleSecurityExpressionHandler simpleSecurityExpressionHandler() {
		Assert.notNull(applicationContext);
		SimpleSecurityExpressionHandler expressionHandler = new SimpleSecurityExpressionHandler();
		if (permissionEvaluators != null && permissionEvaluators.size() == 1)
			expressionHandler.setPermissionEvaluator(permissionEvaluators.get(0));
		expressionHandler.setApplicationContext(applicationContext);
		expressionHandler.setRoleHierarchy(roleHierarchy);
		return expressionHandler;
	}

	@Bean
	@Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
	public SecurityExpressionContextHolder expressionContextHolder() {
		return new SecurityExpressionContextHolderImpl(simpleSecurityExpressionHandler());
	}

	@Bean
	public FilterStringParser filterStringParser() {
		return new FilterStringParser();
	}

	@Bean
	public ExpressionEvaluator expressionEvaluator() {
		return new RequestScopedExpressionEvaluator();
	}

	@Bean
	public ExpressionBuilder expressionBuilder() {
		return new ExpressionBuilder(defaultConversionService, expressionEvaluator());
	}

	@Override
	@Bean
	public PersistentEntityResourceHandlerMethodArgumentResolver persistentEntityArgumentResolver() {
		List<HttpMessageConverter<?>> messageConverters = defaultMessageConverters();
		configureHttpMessageConverters(messageConverters);
		DtoAwarePersistentEntityResourceHandlerMethodArgumentResolver resolver = new DtoAwarePersistentEntityResourceHandlerMethodArgumentResolver(
				messageConverters, repoRequestArgumentResolver(), backendIdHandlerMethodArgumentResolver(),
				new DomainObjectReader(persistentEntities, resourceMappings()), dtoDomainRegistry,
				entityFromDtoCreator());
		resolver.setValidate(true);
		resolver.setValidator(validator);
		resolver.setSpelEvaluatorBean(spelEvaluatorBean());
		return resolver;
	}

	public SimpleModule dtoAwareDeserializerModifierModule() {
		SimpleModule simpleModule = new SimpleModule("ModuleWithDtoAwareDeserializerModifier", new Version(1, 0, 0,
				"SNAPSHOT", "stalkon", "orest"));
		AssociationLinks associationLinks = new AssociationLinks(resourceMappings());
		simpleModule.setDeserializerModifier(new DtoAwareDeserializerModifier(persistentEntities,
				new UriToEntityConverter(persistentEntities(), defaultConversionService()), associationLinks,
				dtoDomainRegistry));
		return simpleModule;

	}

	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper mapper = super.objectMapper();
		mapper.registerModule(dtoAwareDeserializerModifierModule());
		return mapper;
	}

	@Override
	@Bean
	public ObjectMapper halObjectMapper() {
		ObjectMapper mapper = super.halObjectMapper();
		mapper.registerModule(dtoAwareDeserializerModifierModule());
		return mapper;
	}

	@Bean
	public DefaultEntityFromDtoCreator entityFromDtoCreator() {
		DefaultEntityFromDtoCreator creator = new DefaultEntityFromDtoCreator(dtoDomainRegistry, beanFactory,
				persistentEntities());
		return creator;
	}

	@Bean
	public SpelEvaluatorBean spelEvaluatorBean() {
		return new SpelEvaluatorBean(beanFactory);
	}

	@Bean
	public OpenRestEntityLinks entityLinks() {
		return new OpenRestEntityLinks(repositories(), resourceMappings(), config(), pageableResolver(),
				backendIdConverterRegistry());
	}

	@Bean
	public ProjectionInfoRegistry projectionExpandsRegistry() {
		Set<String> packagesToScan = new HashSet<String>();

		for (Class<?> domainType : repositories()) {
			packagesToScan.add(domainType.getPackage().getName());
		}

		Set<Class<?>> projections = new AnnotatedTypeScanner(Projection.class).findTypes(packagesToScan);

		ProjectionInfoRegistry expandsRegistry = new ProjectionInfoRegistry();
		for (Class<?> projection : projections) {
			Projection projectionAnn = AnnotationUtils.findAnnotation(projection, Projection.class);

			Expand expand = AnnotationUtils.findAnnotation(projection, Expand.class);
			Secure secure = AnnotationUtils.findAnnotation(projection, Secure.class);
			for (Class<?> entityType : projectionAnn.types()) {
				PathBuilder<?> builder = new PathBuilderFactory().create(entityType);
				List<Join> expands = null;
				if (expand != null) {
					expands = ProjectionInfoRegistry.getExpands(expand.value(), entityType, builder);
				}
				ProjectionInfo projectionInfo = new ProjectionInfo(expands, secure == null ? null : secure.value());
				expandsRegistry.put(projectionAnn.name(), entityType, projectionInfo);
			}
		}
		return expandsRegistry;
	}

	@Bean
	public DtoDomainRegistry dtoDomainRegistry() throws ClassNotFoundException {
		Set<String> packagesToScan = new HashSet<String>();

		for (Class<?> domainType : repositories()) {
			packagesToScan.add(domainType.getPackage().getName());
		}
		DtoDomainRegistry registry = new DtoDomainRegistry();
		Set<Class<?>> candidates = new AnnotatedTypeScanner(Dto.class).findTypes(packagesToScan);
		for (Class<?> dtoClass : candidates) {
			Dto dtoAnn = AnnotationUtils.findAnnotation(dtoClass, Dto.class);
			Secure secure = AnnotationUtils.findAnnotation(dtoClass, Secure.class);
			DtoInformation dtoInfo = new DtoInformation(dtoAnn.entityType(), dtoAnn.name(), dtoClass,
					dtoAnn.entityCreatorType(), dtoAnn.entityMergerType(), dtoAnn.type(), secure == null ? null
							: secure.value());
			registry.put(dtoClass, dtoInfo);
			if (!dtoInfo.getName().isEmpty())
				registry.put(dtoInfo.getName(), dtoInfo);
		}
		return registry;
	}

	@Override
	public RequestMappingHandlerMapping repositoryExporterHandlerMapping() {
		RequestMappingHandlerMapping mapping = super.repositoryExporterHandlerMapping();
		mapping.setInterceptors(new Object[] { new NonOrestRequestsInterceptor(super.baseUri()) });
		return mapping;
	}

}
