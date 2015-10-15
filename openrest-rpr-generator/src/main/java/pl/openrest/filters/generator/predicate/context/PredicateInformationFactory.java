package pl.openrest.filters.generator.predicate.context;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;

import lombok.Data;

import org.reflections.Reflections;

import pl.openrest.filters.predicate.annotation.Predicate;
import pl.openrest.filters.predicate.annotation.Predicate.PredicateType;
import pl.openrest.filters.remote.predicate.AbstractPredicate;
import pl.openrest.filters.remote.predicate.FilterPredicate;
import pl.openrest.filters.remote.predicate.SearchPredicate;
import pl.openrest.filters.remote.predicate.SortPredicate;

public class PredicateInformationFactory {

    private final Reflections reflections;

    public PredicateInformationFactory(Reflections reflections) {
        this.reflections = reflections;
    }

    public AbstractPredicate from(Method method) {
        Predicate predicateAnn = method.getAnnotation(Predicate.class);
        if (predicateAnn == null)
            throw new IllegalArgumentException("Method should be annotated with @Predicate");

        String name = predicateAnn.name().isEmpty() ? method.getName() : predicateAnn.name();
        PredicateType type = predicateAnn.type();

        switch (type) {
        case SEARCH:
            boolean defaultedPageable = predicateAnn.defaultedPageable();
            return new SearchPredicate(name, defaultedPageable, createParametersInformation(method));
        case FILTER:
            return new FilterPredicate(name, createParametersInformation(method));
        case SORT:
            return new SortPredicate(name, createParametersInformation(method));
        default:
            // should never get here
            return null;
        }
    }

    private ParameterInformation[] createParametersInformation(Method method) {
        Parameter parameters[] = method.getParameters();
        List<String> names = reflections.getMethodParamNames(method);
        ParameterInformation parametersInfo[] = new ParameterInformation[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            parametersInfo[i] = new ParameterInformation(names.get(i), parameters[i].getType());
        }
        return parametersInfo;
    }

    @Data
    public static class ParameterInformation {
        private final String name;
        private final Class<?> type;
    }
}
