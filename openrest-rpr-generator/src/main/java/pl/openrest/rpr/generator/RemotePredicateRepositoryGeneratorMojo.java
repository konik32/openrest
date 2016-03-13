package pl.openrest.rpr.generator;

import java.util.List;
import java.util.Set;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.reflections.Reflections;
import org.reflections.scanners.MethodParameterNamesScanner;
import org.reflections.scanners.Scanner;
import org.reflections.scanners.TypeAnnotationsScanner;

import pl.openrest.filters.predicate.annotation.PredicateRepository;
import pl.openrest.generator.commons.AbstractGeneratorMojo;
import pl.openrest.generator.commons.type.TypeResolver;

@Mojo(name = "process", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class RemotePredicateRepositoryGeneratorMojo extends AbstractGeneratorMojo {

    @Override
    protected void doExecute() throws MojoExecutionException {
        Reflections reflections = configuration.get("reflections");
        TypeResolver defaultTypeResolver = configuration.get("defaultTypeResolver");
        Set<Class<?>> repoTypes = reflections.getTypesAnnotatedWith(PredicateRepository.class);
        for (Class<?> repoType : repoTypes) {
            if (defaultTypeResolver.supports(repoType))
                defaultTypeResolver.resolve(repoType);
        }
    }

    @Override
    protected void addDefaultTypeResolvers(List<TypeResolver> typeResolvers) throws MojoExecutionException, MojoFailureException {
        typeResolvers.add(new PredicateRepositoryResolver());
        super.addDefaultTypeResolvers(typeResolvers);
    }

    @Override
    protected void addDefaultScanners(List<Scanner> scanners) throws MojoExecutionException {
        scanners.add(new TypeAnnotationsScanner());
        scanners.add(new MethodParameterNamesScanner());
    }

}
