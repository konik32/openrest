package pl.openrest.filters.generator.predicate.serializer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.annotation.Generated;
import javax.lang.model.element.Modifier;

import pl.openrest.filters.generator.predicate.context.PredicateInformationFactory.ParameterInformation;

import com.google.common.base.CaseFormat;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.ParameterSpec;

public class JavaSerializerUtils {

    public static ParameterSpec[] createParameters(ParameterInformation[] parameters) {
        ParameterSpec specs[] = new ParameterSpec[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            specs[i] = ParameterSpec.builder(parameters[i].getType(), parameters[i].getName()).build();
        }
        return specs;
    }

    public static FieldSpec createMethodNameStaticField(String name) {
        String upperUnderscoreName = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, name);
        return FieldSpec.builder(String.class, upperUnderscoreName, Modifier.STATIC, Modifier.FINAL, Modifier.PRIVATE)
                .initializer("$S", name).build();
    }

    public static FieldSpec createDefaultedPageableField(boolean defaultedPageable) {
        return FieldSpec.builder(boolean.class, "DEFAULTED_PAGEABLE", Modifier.STATIC, Modifier.FINAL, Modifier.PUBLIC)
                .initializer("$L", defaultedPageable).build();
    }

    public static String[] getParameterNames(List<ParameterSpec> parameterSpecs) {
        String parameterNames[] = new String[parameterSpecs.size()];
        for (int i = 0; i < parameterSpecs.size(); i++) {
            parameterNames[i] = parameterSpecs.get(i).name;
        }
        return parameterNames;
    }

    public static AnnotationSpec createGeneratedAnnotationSpec() {
        return AnnotationSpec.builder(Generated.class).addMember("value", "$S", "pl.openrest.rpr.generator").build();
    }
}
