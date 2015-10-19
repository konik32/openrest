package pl.openrest.rdto.generator;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.junit.runners.Parameterized.Parameter;
import org.reflections.Reflections;
import org.reflections.scanners.Scanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.springframework.util.ReflectionUtils.FieldFilter;

import pl.openrest.dto.annotations.Dto;
import pl.openrest.generator.commons.AbstractGeneratorMojo;
import pl.openrest.generator.commons.Configuration;
import pl.openrest.generator.commons.type.TypeResolver;

@Mojo(name = "process", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class RemoteDtoGeneratorMojo extends AbstractGeneratorMojo {

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
        dtoFieldFilters.add(new NonFinalOrStaticFieldFilter());
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
    }

    @Override
    protected void addDefaultScanners(List<Scanner> scanners) {
        scanners.add(new TypeAnnotationsScanner());
    }

    public void setDtoFieldFilters(List<FieldFilter> dtoFieldFilters) {
        this.dtoFieldFilters = dtoFieldFilters;
    }

}
