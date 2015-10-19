package pl.openrest.generator.commons.type;

import java.util.HashMap;
import java.util.Map;

import javax.lang.model.type.TypeMirror;

import pl.openrest.generator.commons.Configuration;
import pl.openrest.generator.commons.ConfigurationAware;
import pl.openrest.generator.commons.entity.EntityInformation;
import pl.openrest.generator.commons.entity.EntityInformationRegistry;

import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

public class EntityTypeResolver implements TypeResolver, ConfigurationAware {

    private static final Map<String, Object> EMPTY_MAP = new HashMap<String, Object>(0);

    private EntityInformationRegistry entityInfoRegistry;

    @Override
    public void setConfiguration(Configuration configuration) {
        this.entityInfoRegistry = configuration.get("entityInfoRegistry");
    }

    @Override
    public boolean supports(Class<?> type) {
        return entityInfoRegistry.contains(type);
    }

    @Override
    public TypeName resolve(Class<?> type) {
        EntityInformation entityInfo = entityInfoRegistry.get(type);
        if (entityInfo.isExported())
            return TypeName.get(String.class);
        return ParameterizedTypeName.get(Map.class, String.class, Object.class);
    }
}
