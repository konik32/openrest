package pl.openrest.generator.commons.type;

import com.squareup.javapoet.ClassName;

public interface TypeResolver {

    boolean supports(Class<?> type);

    ClassName resolve(Class<?> type);

}
