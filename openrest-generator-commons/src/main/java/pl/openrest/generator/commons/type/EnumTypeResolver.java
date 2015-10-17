package pl.openrest.generator.commons.type;

import javax.lang.model.element.Modifier;

import pl.openrest.generator.commons.Configuration;
import pl.openrest.generator.commons.ConfigurationAware;
import pl.openrest.generator.commons.RemoteClassNamingStrategy;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeSpec;

public class EnumTypeResolver implements TypeResolver, ConfigurationAware {

    private RemoteClassNamingStrategy namingStrategy;
    private TypeFileWriter typeFileWriter;

    @Override
    public boolean supports(Class<?> type) {
        return type.isEnum();
    }

    @Override
    public ClassName resolve(Class<?> type) {
        String name = namingStrategy.getClassName(type.getSimpleName());
        String packageName = namingStrategy.getPackageName(type.getPackage().getName());
        TypeSpec.Builder builder = TypeSpec.enumBuilder(name).addModifiers(Modifier.PUBLIC);
        for (int i = 0; i < type.getEnumConstants().length; i++) {
            builder.addEnumConstant(type.getEnumConstants()[i].toString());
        }
        typeFileWriter.write(builder.build(), packageName);
        return ClassName.get(packageName, name);
    }

    @Override
    public void setConfiguration(Configuration configuration) {
        this.namingStrategy = configuration.get("defaultNamingStrategy");
        this.typeFileWriter = configuration.get("typeFileWriter");
    }

}
