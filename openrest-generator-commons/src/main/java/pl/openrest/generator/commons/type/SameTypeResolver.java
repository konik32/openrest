package pl.openrest.generator.commons.type;

import com.squareup.javapoet.TypeName;

public class SameTypeResolver implements TypeResolver {

    @Override
    public boolean supports(Class<?> type) {
        return true;
    }

    @Override
    public TypeName resolve(Class<?> type) {
        return TypeName.get(type);
    }

}
