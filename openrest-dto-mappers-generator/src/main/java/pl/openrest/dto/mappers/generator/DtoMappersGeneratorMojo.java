package pl.openrest.dto.mappers.generator;

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
import org.springframework.util.ReflectionUtils.FieldFilter;

import pl.openrest.dto.annotations.Dto;
import pl.openrest.generator.commons.AbstractGeneratorMojo;
import pl.openrest.generator.commons.Configuration;
import pl.openrest.generator.commons.type.CompositeFieldFilter;
import pl.openrest.generator.commons.type.NonFinalOrStaticFieldFilter;
import pl.openrest.generator.commons.type.TypeResolver;

@Mojo(name = "process", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class DtoMappersGeneratorMojo extends AbstractGeneratorMojo {

    @Parameter
    private List<FieldFilter> dtoFieldFilters = new LinkedList<>();

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
        return configuration;
    };

    @Override
    protected void addDefaultTypeResolvers(List<TypeResolver> typeResolvers) {
        DtoMapperResolver dtoMapperResolver = new DtoMapperResolver(new CompositeFieldFilter(dtoFieldFilters));
        typeResolvers.add(dtoMapperResolver);
        super.addDefaultTypeResolvers(typeResolvers);
    }

    @Override
    protected void initializeDefault() {
        if (defaultNamingStrategy == null)
            defaultNamingStrategy = new MapperNamingStrategy();
        super.initializeDefault();
        if (dtoFieldFilters == null) {
            dtoFieldFilters = new LinkedList<>();
        }
        dtoFieldFilters.add(new NonFinalOrStaticFieldFilter());
    }

    @Override
    protected void addDefaultScanners(List<Scanner> scanners) {
        scanners.add(new TypeAnnotationsScanner());
    }

    public void setDtoFieldFilters(List<FieldFilter> dtoFieldFilters) {
        this.dtoFieldFilters = dtoFieldFilters;
    }

}