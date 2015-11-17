package pl.openrest.rdto.generator;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.reflections.Reflections;
import org.reflections.scanners.Scanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils.FieldFilter;

import pl.openrest.dto.annotations.Dto;
import pl.openrest.generator.commons.AbstractGeneratorMojo;
import pl.openrest.generator.commons.Configuration;
import pl.openrest.generator.commons.entity.DefaultEntityInformationRegistry;
import pl.openrest.generator.commons.type.CompositeFieldFilter;
import pl.openrest.generator.commons.type.EntityTypeResolver;
import pl.openrest.generator.commons.type.NonFinalOrStaticFieldFilter;
import pl.openrest.generator.commons.type.TypeResolver;

@Mojo(name = "process", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class RemoteDtoGeneratorMojo extends AbstractGeneratorMojo {

    @Parameter
    private List<FieldFilter> dtoFieldFilters = new LinkedList<>();

    @Parameter
    private List<String> entityAnnotations;

    @Override
    protected void doExecute() throws MojoExecutionException {
        Reflections reflections = configuration.get("reflections");
        TypeResolver defaultTypeResolver = configuration.get("defaultTypeResolver");
        Set<Class<?>> dtoTypes = reflections.getTypesAnnotatedWith(Dto.class);
        for (Class<?> dtoType : dtoTypes) {
            if (defaultTypeResolver.supports(dtoType))
                defaultTypeResolver.resolve(dtoType);
        }
    }

    @Override
    protected Configuration createConfiguration() {
        Configuration configuration = super.createConfiguration();
        configuration.put("dtoFieldFilters", dtoFieldFilters);
        configuration.put("entityAnnotations", getEntityAnnotations());
        configuration.put("entityInfoRegistry", new DefaultEntityInformationRegistry());
        return configuration;
    };

    @Override
    protected void addDefaultTypeResolvers(List<TypeResolver> typeResolvers) {
        dtoFieldFilters.add(new NonFinalOrStaticFieldFilter());
        typeResolvers.add(new EntityTypeResolver());
        DtoTypeResolver dtoTypeResolver = new DtoTypeResolver(new CompositeFieldFilter(dtoFieldFilters));
        typeResolvers.add(dtoTypeResolver);
        super.addDefaultTypeResolvers(typeResolvers);
    }

    @Override
    protected void initializeDefault() {
        super.initializeDefault();
        if (dtoFieldFilters == null) {
            dtoFieldFilters = new LinkedList<>();
        }
        if (entityAnnotations == null) {
            entityAnnotations = Arrays.asList("javax.persistence.Entity", "javax.persistence.Embeddable");
        }
    }

    @Override
    protected void addDefaultScanners(List<Scanner> scanners) {
        scanners.add(new TypeAnnotationsScanner());
    }

    private List<Class<? extends Annotation>> getEntityAnnotations() {
        List<Class<? extends Annotation>> annotations = new ArrayList<>(entityAnnotations.size());
        for (String annName : entityAnnotations) {
            try {
                annotations.add((Class<? extends Annotation>) ClassUtils.forName(annName, RemoteDtoGeneratorMojo.class.getClassLoader()));
            } catch (ClassNotFoundException | LinkageError e) {
                new IllegalArgumentException(String.format("Error occured while loading a class: %s", annName), e);
            }
        }
        return annotations;
    }

    public void setDtoFieldFilters(List<FieldFilter> dtoFieldFilters) {
        this.dtoFieldFilters = dtoFieldFilters;
    }

}
