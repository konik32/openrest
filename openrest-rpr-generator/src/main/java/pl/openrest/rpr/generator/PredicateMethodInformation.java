package pl.openrest.rpr.generator;

import lombok.Data;
import pl.openrest.filters.remote.predicate.AbstractPredicate;

import com.google.common.base.CaseFormat;

@Data
public class PredicateMethodInformation {

    private final String name;
    private final boolean defaultedPageable;
    private final Class<? extends AbstractPredicate> returnType;
    private final ParameterInformation parametersInfo[];

    public PredicateMethodInformation(String name, boolean defaultedPageable, Class<? extends AbstractPredicate> returnType,
            ParameterInformation... parametersInfo) {
        this.name = name;
        this.defaultedPageable = defaultedPageable;
        this.returnType = returnType;
        this.parametersInfo = parametersInfo;
    }

    public String getUpperCaseName() {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, name);
    }

    public String[] getParameterNames() {
        String parameterNames[] = new String[parametersInfo.length];
        for (int i = 0; i < parametersInfo.length; i++) {
            parameterNames[i] = parametersInfo[i].name;
        }
        return parameterNames;
    }

    @Data
    public static class ParameterInformation {
        private final String name;
        private final Class<?> type;

    }

}
