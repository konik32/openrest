package pl.openrest.rpr.generator;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;

import org.reflections.Reflections;

import pl.openrest.filters.predicate.annotation.Predicate;
import pl.openrest.filters.remote.predicate.AbstractPredicate;
import pl.openrest.filters.remote.predicate.FilterPredicate;
import pl.openrest.filters.remote.predicate.SearchPredicate;
import pl.openrest.filters.remote.predicate.SortPredicate;
import pl.openrest.rpr.generator.PredicateMethodInformation.ParameterInformation;

public class PredicateMethodInformationFactory {
    private final Reflections reflections;

    public PredicateMethodInformationFactory(Reflections reflections) {
        this.reflections = reflections;
    }

    public PredicateMethodInformation create(Method method, Predicate predicateAnn) {
        String name = predicateAnn.name().isEmpty() ? method.getName() : predicateAnn.name();
        boolean defaultedPageable = predicateAnn.defaultedPageable();
        Class<? extends AbstractPredicate> returnType = getReturnType(predicateAnn);
        ParameterInformation parametersInfo[] = createParametersInformation(method);
        return new PredicateMethodInformation(name, defaultedPageable, returnType, parametersInfo);
    }

    private Class<? extends AbstractPredicate> getReturnType(Predicate predicateAnn) {
        switch (predicateAnn.type()) {
        case SEARCH:
            return SearchPredicate.class;
        case SORT:
            return SortPredicate.class;
        default:
            return FilterPredicate.class;
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

}
