package pl.openrest.rpr.generator;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Modifier;

import pl.openrest.filters.remote.predicate.SearchPredicate;
import pl.openrest.generator.commons.type.TypeResolver;
import pl.openrest.rpr.generator.PredicateMethodInformation.ParameterInformation;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;

public class PredicateMethodSpecFactory {

    private static String SEARCH_PREDICATE_WITH_PARAMS_FORMAT = "return new $T($L,$L,$L)";
    private static String SEARCH_PREDICATE_WITHOUT_PARAMS_FORMAT = "return new $T($L,$L)";
    private static String FILTER_PREDICATE_WITH_PARAMS_FORMAT = "return new $T($L,$L)";
    private static String FILTER_PREDICATE_WITHOUT_PARAMS_FORMAT = "return new $T($L)";

    private final TypeResolver typeResolver;

    public PredicateMethodSpecFactory(TypeResolver typeResolver) {
        this.typeResolver = typeResolver;
    }

    public MethodSpec create(PredicateMethodInformation predicateMethodInfo) {

        MethodSpec.Builder builder = MethodSpec.methodBuilder(predicateMethodInfo.getName()).returns(predicateMethodInfo.getReturnType())
                .addParameters(createParameters(predicateMethodInfo.getParametersInfo())).addModifiers(Modifier.PUBLIC, Modifier.STATIC);
        builder.addCode(createMethodCodeBlock(predicateMethodInfo));
        return builder.build();
    }

    private static CodeBlock createMethodCodeBlock(PredicateMethodInformation predicateMethodInfo) {
        CodeBlock.Builder builder = CodeBlock.builder();
        String parameterNames[] = predicateMethodInfo.getParameterNames();
        if (predicateMethodInfo.getReturnType().equals(SearchPredicate.class)) {
            if (parameterNames.length > 0)
                builder.addStatement(SEARCH_PREDICATE_WITH_PARAMS_FORMAT, predicateMethodInfo.getReturnType(),
                        predicateMethodInfo.getUpperCaseName(), predicateMethodInfo.isDefaultedPageable(), String.join(",", parameterNames));
            else
                builder.addStatement(SEARCH_PREDICATE_WITHOUT_PARAMS_FORMAT, predicateMethodInfo.getReturnType(),
                        predicateMethodInfo.getUpperCaseName(), predicateMethodInfo.isDefaultedPageable());
        } else {
            if (parameterNames.length > 0)
                builder.addStatement(FILTER_PREDICATE_WITH_PARAMS_FORMAT, predicateMethodInfo.getReturnType(),
                        predicateMethodInfo.getUpperCaseName(), String.join(",", parameterNames));
            else
                builder.addStatement(FILTER_PREDICATE_WITHOUT_PARAMS_FORMAT, predicateMethodInfo.getReturnType(),
                        predicateMethodInfo.getUpperCaseName());
        }
        return builder.build();
    }

    public List<ParameterSpec> createParameters(ParameterInformation[] parameters) {
        List<ParameterSpec> specs = new ArrayList<>(parameters.length);
        for (int i = 0; i < parameters.length; i++) {
            specs.add(ParameterSpec.builder(typeResolver.resolve(parameters[i].getType()), parameters[i].getName()).build());
        }
        return specs;
    }

}
