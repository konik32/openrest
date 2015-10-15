package pl.openrest.filters.generator.predicate.context;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.openrest.filters.predicate.annotation.Predicate;
import pl.openrest.filters.predicate.annotation.PredicateRepository;
import pl.openrest.filters.remote.predicate.AbstractPredicate;

public class PredicateRepositoryInformationFactory {

    private final PredicateInformationFactory predicateInfoFactory;

    public PredicateRepositoryInformationFactory(PredicateInformationFactory predicateInfoFactory) {
        this.predicateInfoFactory = predicateInfoFactory;
    }

    public PredicateRepositoryInformation from(Class<?> type) {
        PredicateRepository repositoryAnn = type.getAnnotation(PredicateRepository.class);
        if (repositoryAnn == null)
            throw new IllegalArgumentException("Passed class should be annotated with @PredicateRepository");

        PredicateRepositoryInformation repoInfo = new PredicateRepositoryInformation(type, repositoryAnn.value(), type.getPackage()
                .getName(), repositoryAnn.defaultedPageable());
        for (Method method : type.getMethods()) {
            Predicate predicateAnn = method.getAnnotation(Predicate.class);
            if (predicateAnn == null)
                continue;
            AbstractPredicate predicate = predicateInfoFactory.from(method);
            repoInfo.addPredicate(predicate);
        }
        return repoInfo;
    }

}
