package pl.openrest.filters.generator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.reflections.Reflections;
import org.reflections.scanners.MethodParameterNamesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ConfigurationBuilder;

import pl.openrest.filters.generator.predicate.context.PredicateInformationFactory;
import pl.openrest.filters.generator.predicate.context.PredicateRepositoryInformation;
import pl.openrest.filters.generator.predicate.context.PredicateRepositoryInformationFactory;
import pl.openrest.filters.generator.predicate.serializer.DefaultRemotePredicateRepositoryNamingStrategy;
import pl.openrest.filters.generator.predicate.serializer.JavaRemotePredicateRepositorySerializer;
import pl.openrest.filters.generator.predicate.serializer.RemotePredicateRepositoryNamingStrategy;
import pl.openrest.filters.generator.predicate.serializer.RemotePredicateRepositorySerializer;
import pl.openrest.filters.predicate.annotation.PredicateRepository;

/**
 * @goal process
 * @phase process-sources
 */
public class RemotePredicateRepositoryGeneratorMojo extends AbstractMojo {
    /**
     * 
     * @parameter expression="${project.build.directory}/generated-sources/java"
     * @required
     */
    private File outputDirectory;

    public void execute() throws MojoExecutionException {
        Reflections reflections = new Reflections(new ConfigurationBuilder().forPackages("example").addScanners(
                new MethodParameterNamesScanner(), new TypeAnnotationsScanner()));

        RemotePredicateRepositoryNamingStrategy namingStrategy = new DefaultRemotePredicateRepositoryNamingStrategy();
        RemotePredicateRepositorySerializer serializer = new JavaRemotePredicateRepositorySerializer(outputDirectory, namingStrategy);
        PredicateInformationFactory predicateInfoFactory = new PredicateInformationFactory(reflections);
        PredicateRepositoryInformationFactory repoFactory = new PredicateRepositoryInformationFactory(predicateInfoFactory);

        Set<Class<?>> repositoriesTypes = reflections.getTypesAnnotatedWith(PredicateRepository.class);
        List<PredicateRepositoryInformation> repositoriesInfo = new ArrayList<PredicateRepositoryInformation>(repositoriesTypes.size());

        
        for (Class repoType : repositoriesTypes) {
            repositoriesInfo.add(repoFactory.from(repoType));
        }
        try {
            serializer.serialize(repositoriesInfo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
