package pl.openrest.rdto.generator;

import java.lang.reflect.Field;
import java.util.List;

import org.springframework.util.ReflectionUtils.FieldFilter;

public class CompositeFieldFilter implements FieldFilter {

    private final List<FieldFilter> filters;

    public CompositeFieldFilter(List<FieldFilter> filters) {
        this.filters = filters;
    }

    @Override
    public boolean matches(Field field) {
        for (FieldFilter filter : filters) {
            if (!filter.matches(field))
                return false;
        }
        return true;
    }

}
