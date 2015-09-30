package pl.openrest.dto.config;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.mapping.context.PersistentEntities;
import org.springframework.data.repository.support.Repositories;
import org.springframework.data.rest.core.UriToEntityConverter;
import org.springframework.data.rest.core.mapping.ResourceMappings;
import org.springframework.data.util.AnnotatedTypeScanner;
import org.springframework.http.converter.HttpMessageConverter;

import pl.openrest.core.config.OpenRestConfigurer;
import pl.openrest.dto.annotations.Dto;
import pl.openrest.dto.event.AnnotatedDtoEventHandlerBeanPostProcessor;
import pl.openrest.dto.mapper.BeforeCreateMappingHandler;
import pl.openrest.dto.mapper.BeforeUpdateMappingHandler;
import pl.openrest.dto.mapper.DefaultCreateAndUpdateMapper;
import pl.openrest.dto.mapper.MapperDelegator;
import pl.openrest.dto.mapper.MapperFactory;
import pl.openrest.dto.mapper.MappingManager;
import pl.openrest.dto.registry.DtoInformation;
import pl.openrest.dto.registry.DtoInformationRegistry;
import pl.openrest.dto.spel.evaluation.SpelEvaluatorBean;
import pl.openrest.dto.webmvc.NonDtoRequestsFilter;
import pl.openrest.dto.webmvc.json.DtoAwareDeserializerModifier;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

@Configuration
public class OpenRestDtoConfiguration {

    @Autowired
    private Repositories repositories;
    @Autowired
    private List<HttpMessageConverter<?>> defaultMessageConverters;

    @Bean
    public DtoInformationRegistry dtoInformationRegistry() {
        Set<String> packagesToScan = new HashSet<String>();

        for (Class<?> domainType : repositories) {
            packagesToScan.add(domainType.getPackage().getName());
        }
        DtoInformationRegistry registry = new DtoInformationRegistry();
        Set<Class<?>> candidates = new AnnotatedTypeScanner(Dto.class).findTypes(packagesToScan);
        for (Class<?> dtoClass : candidates) {
            Dto dtoAnn = AnnotationUtils.findAnnotation(dtoClass, Dto.class);
            DtoInformation dtoInfo = new DtoInformation(dtoClass, dtoAnn);
            registry.put(dtoClass, dtoInfo);
            if (!dtoInfo.getName().isEmpty())
                registry.put(dtoInfo.getName(), dtoInfo);
        }
        return registry;
    }

    @Bean
    public static AnnotatedDtoEventHandlerBeanPostProcessor annotatedDtoEventHandlerBeanPostProcessor() {
        return new AnnotatedDtoEventHandlerBeanPostProcessor();
    }

    @Bean
    public MapperFactory mapperFactory() {
        return new MapperFactory();
    }

    @Autowired
    public void setDefaultMappers(MapperFactory mapperFactory, PersistentEntities persistentEntities,
            DtoInformationRegistry dtoInformationRegistry) {
        DefaultCreateAndUpdateMapper defaultCreateAndUpdateMapper = new DefaultCreateAndUpdateMapper(dtoInformationRegistry,
                mapperDelegator(), persistentEntities);
        mapperFactory.setDefaultCreateMapper(defaultCreateAndUpdateMapper);
        mapperFactory.setDefaultUpdateMapper(defaultCreateAndUpdateMapper);
    }

    @Bean
    public MapperDelegator mapperDelegator() {
        return new MapperDelegator(mapperFactory());
    }

    @Bean
    public MappingManager mappingManager() {
        return new MappingManager(mapperDelegator(), defaultMessageConverters, dtoInformationRegistry());
    }

    @Bean
    public NonDtoRequestsFilter nonDtoRequestFilter() {
        return new NonDtoRequestsFilter();
    }

    @Bean
    public OpenRestConfigurer openRestDtoConfigurer() {
        return new OpenRestDtoConfigurer();
    }

    @Autowired
    public void addSpelEvaluatorHandler(MappingManager mappingManager, BeanFactory beanFactory) {
        SpelEvaluatorBean spelEvaluator = new SpelEvaluatorBean(beanFactory);
        mappingManager.addHandler((BeforeCreateMappingHandler) spelEvaluator);
        mappingManager.addHandler((BeforeUpdateMappingHandler) spelEvaluator);
    }

    @Autowired
    public void configureObjectMapper(ObjectMapper objectMapper, PersistentEntities persistentEntities,
            ConversionService defaultConversionService, ResourceMappings resourceMappings, DtoInformationRegistry dtoInformationRegistry) {
        objectMapper.registerModule(dtoAwareDeserializerModifierModule(persistentEntities, defaultConversionService, resourceMappings,
                dtoInformationRegistry));
    }

    @Autowired
    public void configureHalObjectMapper(ObjectMapper halObjectMapper, PersistentEntities persistentEntities,
            ConversionService defaultConversionService, ResourceMappings resourceMappings, DtoInformationRegistry dtoInformationRegistry) {
        halObjectMapper.registerModule(dtoAwareDeserializerModifierModule(persistentEntities, defaultConversionService, resourceMappings,
                dtoInformationRegistry));
    }

    private SimpleModule dtoAwareDeserializerModifierModule(PersistentEntities persistentEntities,
            ConversionService defaultConversionService, ResourceMappings resourceMappings, DtoInformationRegistry dtoInformationRegistry) {
        SimpleModule simpleModule = new SimpleModule("ModuleWithDtoAwareDeserializerModifier", new Version(1, 0, 0, "SNAPSHOT", "stalkon",
                "orest"));
        simpleModule.setDeserializerModifier(new DtoAwareDeserializerModifier(new UriToEntityConverter(persistentEntities,
                defaultConversionService), resourceMappings, dtoInformationRegistry));
        return simpleModule;

    }

}
