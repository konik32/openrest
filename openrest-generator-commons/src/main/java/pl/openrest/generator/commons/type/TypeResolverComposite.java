package pl.openrest.generator.commons.type;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import pl.openrest.generator.commons.Configuration;
import pl.openrest.generator.commons.ConfigurationAware;

import com.squareup.javapoet.ClassName;

public class TypeResolverComposite implements TypeResolver, ConfigurationAware {

    private final List<TypeResolver> resolvers = new LinkedList<TypeResolver>();
    private final Map<Class<?>, ClassName> resolvedTypeRegistry = new HashMap<>();

    public TypeResolverComposite(List<TypeResolver> resolvers) {
        this.resolvers.addAll(resolvers);
    }

    @Override
    public boolean supports(Class<?> type) {
        for (TypeResolver resolver : resolvers) {
            if (resolver.supports(type)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public ClassName resolve(Class<?> type) {
        if (resolvedTypeRegistry.containsKey(type))
            return resolvedTypeRegistry.get(type);
        for (TypeResolver resolver : resolvers) {
            if (resolver.supports(type)) {
                ClassName className = resolver.resolve(type);
                resolvedTypeRegistry.put(type, className);
                return className;
            }
        }
        // should never get here
        return null;
    }

    @Override
    public void setConfiguration(Configuration configuration) {
        for (TypeResolver resolver : resolvers) {
            if (resolver instanceof ConfigurationAware) {
                ((ConfigurationAware) resolver).setConfiguration(configuration);
            }
        }
    }
}
