package pl.openrest.dto.mappers.generator;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
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

@Mojo(name = "process", defaultPhase = LifecyclePhase.PROCESS_CLASSES)
public class DtoMappersGeneratorMojo extends AbstractGeneratorMojo {

    @Parameter
    private List<FieldFilter> dtoFieldFilters = new LinkedList<>();

    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject mavenProject;

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
    protected Configuration createConfiguration() throws MojoExecutionException {
        Configuration configuration = super.createConfiguration();
        configuration.put("dtoFieldFilters", dtoFieldFilters);
        return configuration;
    };

    @Override
    protected void addDefaultTypeResolvers(List<TypeResolver> typeResolvers) throws MojoExecutionException {
        DtoMapperResolver dtoMapperResolver = new DtoMapperResolver(new CompositeFieldFilter(dtoFieldFilters));
        typeResolvers.add(dtoMapperResolver);
        super.addDefaultTypeResolvers(typeResolvers);
    }

    @Override
    protected void initializeDefault() throws MojoExecutionException {
        if (defaultNamingStrategy == null)
            defaultNamingStrategy = new MapperNamingStrategy();
        if (reflectionsFactory == null)
            reflectionsFactory = new ProjectClassPathAwareReflectionsRepository(mavenProject);
        if (dtoFieldFilters == null) {
            dtoFieldFilters = new LinkedList<>();
        }
        dtoFieldFilters.add(new NonFinalOrStaticFieldFilter());
        super.initializeDefault();
    }

    @Override
    protected void addDefaultScanners(List<Scanner> scanners) {
        scanners.add(new TypeAnnotationsScanner());
    }

    public void setDtoFieldFilters(List<FieldFilter> dtoFieldFilters) {
        this.dtoFieldFilters = dtoFieldFilters;
    }

}
