package pl.openrest.generator.commons.type;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.springframework.util.ReflectionUtils.FieldFilter;

public class NonFinalOrStaticFieldFilter implements FieldFilter {

    @Override
    public boolean matches(Field field) {
        int modifiers = field.getModifiers();
        return !(Modifier.isFinal(modifiers) || Modifier.isStatic(modifiers));
    }

}
