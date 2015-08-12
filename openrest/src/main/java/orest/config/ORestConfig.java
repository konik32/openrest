package orest.config;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import orest.authorization.annotation.Secure;
import orest.dto.DefaultEntityFromDtoCreatorAndMerger;
import orest.dto.Dto;
import orest.dto.DtoDomainRegistry;
import orest.dto.DtoInformation;
import orest.dto.DtoToEntityConversionManager;
import orest.dto.authorization.AuthorizeDtoAnnotationAuthorizationStrategy;
import orest.dto.authorization.DtoAuthorizationStratetyFactory;
import orest.dto.authorization.SecureAnnotationAuthorizationStrategy;
import orest.dto.authorization.SpringSecurityAuthorizationStrategyDtoHandler;
import orest.dto.expression.spel.SpelEvaluatorBean;
import orest.dto.handler.DtoHandler;
import orest.dto.validation.UpdateValidationContext;
import orest.dto.validation.UpdateValidationContextHandler;
import orest.dto.validation.ValidatorInvoker;
import orest.event.AnnotatedDtoEventHandlerBeanPostProcessor;
import orest.expression.ExpressionBuilder;
import orest.expression.registry.EntityExpressionMethodsRegistry;
import orest.expression.registry.Expand;
import orest.expression.registry.ExpressionMethodInformation.Join;
import orest.expression.registry.ProjectionInfo;
import orest.expression.registry.ProjectionInfoRegistry;
import orest.json.DtoAwareDeserializerModifier;
import orest.mvc.DefaultedQPageableHandlerMethodArgumentResolver;
import orest.mvc.HateoasQSortHandlerMethodArgumentResolver;
import orest.mvc.NonOrestRequestsInterceptor;
import orest.mvc.QSortMethodArgumentResolver;
import orest.parser.FilterStringParser;
import orest.security.ExpressionEvaluator;
import orest.security.RequestScopedExpressionEvaluator;
import orest.security.SecurityExpressionContextHolder;
import orest.security.SecurityExpressionContextHolderImpl;
import orest.security.SimpleSecurityExpressionHandler;
import orest.webmvc.support.OpenRestEntityLinks;
import orest.webmvc.support.PageAndSortUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.DtoAwarePersistentEntityResourceHandlerMethodArgumentResolver;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;
import org.springframework.data.util.AnnotatedTypeScanner;
import org.springframework.data.web.HateoasSortHandlerMethodArgumentResolver;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.util.Assert;
import org.springframework.validation.Validator;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
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

	private static final Logger LOGGER = LoggerFactory.getLogger(ORestConfig.class);

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

	@Autowired
	private PageableHandlerMethodArgumentResolver pageableResolver;

	@Autowired(required = false)
	private DtoAuthorizationStratetyFactory strategyFactory;

	@Autowired
	private RepositoryRestConfiguration config;

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
		if (permissionEvaluators != null) {
			if (permissionEvaluators.size() == 1) {
				expressionHandler.setPermissionEvaluator(permissionEvaluators.get(0));
			} else {
				LOGGER.debug("Not autwiring PermissionEvaluator since size != 1. Got " + permissionEvaluators);
			}
		}
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

	@Bean
	public DtoAwarePersistentEntityResourceHandlerMethodArgumentResolver dtoAwarepersistentEntityArgumentResolver() {
		DtoAwarePersistentEntityResourceHandlerMethodArgumentResolver resolver = new DtoAwarePersistentEntityResourceHandlerMethodArgumentResolver(
				repoRequestArgumentResolver(), backendIdHandlerMethodArgumentResolver(), dtoToEntityConversionManager());
		return resolver;
	}

	protected DtoToEntityConversionManager dtoToEntityConversionManager() {
		List<HttpMessageConverter<?>> messageConverters = defaultMessageConverters();
		configureHttpMessageConverters(messageConverters);
		DtoToEntityConversionManager conversionManager = new DtoToEntityConversionManager(entityFromDtoCreator(),
				messageConverters, dtoDomainRegistry);
		addDtoHandlers(conversionManager);
		return conversionManager;
	}

	public void addDtoHandlers(DtoToEntityConversionManager conversionManager) {
		conversionManager.addHandler(spelEvaluatorBean());
		conversionManager.addHandler(authorizationStrategyDtoHandler());
		conversionManager.addHandler(updateValidationContextHandler());
		conversionManager.addHandler(validatorInvoker());
	}

	@Bean
	public UpdateValidationContextHandler updateValidationContextHandler() {
		return new UpdateValidationContextHandler();
	}

	protected DtoHandler validatorInvoker() {
		ValidatorInvoker invoker = new ValidatorInvoker();
		invoker.addValidator(validator);
		return invoker;
	}

	protected DtoHandler authorizationStrategyDtoHandler() {
		SpringSecurityAuthorizationStrategyDtoHandler handler = new SpringSecurityAuthorizationStrategyDtoHandler();
		handler.addStrategy(new SecureAnnotationAuthorizationStrategy());
		if (strategyFactory != null)
			handler.addStrategy(new AuthorizeDtoAnnotationAuthorizationStrategy(strategyFactory));
		return handler;
	}

	public SimpleModule dtoAwareDeserializerModifierModule() {
		SimpleModule simpleModule = new SimpleModule("ModuleWithDtoAwareDeserializerModifier", new Version(1, 0, 0,
				"SNAPSHOT", "stalkon", "orest"));
		simpleModule.setDeserializerModifier(new DtoAwareDeserializerModifier(persistentEntities,
				new UriToEntityConverter(persistentEntities(), defaultConversionService()), resourceMappings(),
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
	public DefaultEntityFromDtoCreatorAndMerger entityFromDtoCreator() {
		DefaultEntityFromDtoCreatorAndMerger creator = new DefaultEntityFromDtoCreatorAndMerger(dtoDomainRegistry,
				beanFactory, persistentEntities());
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
				ProjectionInfo projectionInfo = new ProjectionInfo(expands, secure != null ? secure.value() : null);
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
					dtoAnn.entityCreatorType(), dtoAnn.entityMergerType(), dtoAnn.type());
			registry.put(dtoClass, dtoInfo);
			if (!dtoInfo.getName().isEmpty())
				registry.put(dtoInfo.getName(), dtoInfo);
		}
		return registry;
	}

	@Bean
	public PageAndSortUtils pageAndSortUtils() {
		return new PageAndSortUtils(expressionBuilder(), filterStringParser());
	}

	protected QSortMethodArgumentResolver qSortmethodArgumentResolver() {
		return new QSortMethodArgumentResolver(sortResolver(), pageAndSortUtils(),
				resourceMetadataHandlerMethodArgumentResolver(), entityExpressionMethodsRegistry());
	}

	protected DefaultedQPageableHandlerMethodArgumentResolver defaultedPageableResolver() {
		return new DefaultedQPageableHandlerMethodArgumentResolver(pageableResolver, pageAndSortUtils(),
				resourceMetadataHandlerMethodArgumentResolver(), entityExpressionMethodsRegistry());
	}

	@Override
	public RequestMappingHandlerMapping repositoryExporterHandlerMapping() {
		RequestMappingHandlerMapping mapping = super.repositoryExporterHandlerMapping();
		mapping.setInterceptors(new Object[] { new NonOrestRequestsInterceptor(super.baseUri()) });
		return mapping;
	}

	@Override
	protected List<HandlerMethodArgumentResolver> defaultMethodArgumentResolvers() {
		// TODO Auto-generated method stub
		List<HandlerMethodArgumentResolver> resolvers = super.defaultMethodArgumentResolvers();
		List<HandlerMethodArgumentResolver> newResolvers = new ArrayList<HandlerMethodArgumentResolver>(
				resolvers.size() + 1);
		newResolvers.add(defaultedPageableResolver());
		for (int i = 1; i < resolvers.size(); i++) {
			newResolvers.add(resolvers.get(i));
		}
		newResolvers.add(qSortmethodArgumentResolver());
		newResolvers.add(dtoAwarepersistentEntityArgumentResolver());
		return newResolvers;
	}

	@Bean
	@Override
	public HateoasSortHandlerMethodArgumentResolver sortResolver() {
		HateoasQSortHandlerMethodArgumentResolver resolver = new HateoasQSortHandlerMethodArgumentResolver();
		resolver.setSortParameter(config().getSortParamName());
		return resolver;
	}

	@Bean
	public static AnnotatedDtoEventHandlerBeanPostProcessor annotatedHandlerBeanPostProcessor() {
		return new AnnotatedDtoEventHandlerBeanPostProcessor();
	}
}
