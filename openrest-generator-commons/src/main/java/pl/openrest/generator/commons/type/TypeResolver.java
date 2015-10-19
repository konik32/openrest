package pl.openrest.generator.commons.type;

import com.squareup.javapoet.TypeName;

public interface TypeResolver {

    boolean supports(Class<?> type);

    TypeName resolve(Class<?> type);

}
