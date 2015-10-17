package pl.openrest.rdto.generator;

import java.lang.reflect.Field;

import javax.annotation.Generated;
import javax.lang.model.element.Modifier;

import lombok.Getter;
import lombok.Setter;

import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;
import org.springframework.util.ReflectionUtils.FieldFilter;

import pl.openrest.dto.annotations.Dto;
import pl.openrest.generator.commons.Configuration;
import pl.openrest.generator.commons.ConfigurationAware;
import pl.openrest.generator.commons.RemoteClassNamingStrategy;
import pl.openrest.generator.commons.type.TypeFileWriter;
import pl.openrest.generator.commons.type.TypeResolver;
import pl.openrest.remote.dto.RemoteCreateDto;
import pl.openrest.remote.dto.RemoteMergeDto;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

public class DtoTypeResolver implements TypeResolver, ConfigurationAware {

    private static final String DTO_NAME_FIELD_NAME = "DTO_NAME";
    private static final String GENERATOR_NAME = "pl.openrest.rdto.generator";

    private RemoteClassNamingStrategy namingStrategy;
    private TypeFileWriter typeFileWriter;
    private TypeResolver typeResolver;
    private final FieldFilter fieldFilter;

    public DtoTypeResolver(FieldFilter fieldFilter) {
        this.fieldFilter = fieldFilter;
    }

    @Override
    public boolean supports(Class<?> type) {
        return type.getAnnotation(Dto.class) != null;
    }

    @Override
    public ClassName resolve(Class<?> type) {
        String className = namingStrategy.getClassName(type.getSimpleName());
        String packageName = namingStrategy.getPackageName(type.getPackage().getName());

        TypeSpec.Builder builder = TypeSpec.classBuilder(className).addModifiers(Modifier.PUBLIC);
        appendAnnotations(builder);
        appendFields(builder, type);
        Dto dtoAnn = type.getAnnotation(Dto.class);

        appendDtoNameGetter(builder, dtoAnn);
        appendInterface(builder, dtoAnn);
        typeFileWriter.write(builder.build(), packageName);
        return ClassName.get(packageName, className);
    }

    private void appendAnnotations(TypeSpec.Builder builder) {
        builder.addAnnotation(Getter.class);
        builder.addAnnotation(Setter.class);
        builder.addAnnotation(AnnotationSpec.builder(Generated.class).addMember("value", "$S", GENERATOR_NAME).build());
    }

    private void appendDtoNameGetter(TypeSpec.Builder builder, Dto dtoAnn) {
        builder.addField(FieldSpec.builder(String.class, DTO_NAME_FIELD_NAME, Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                .initializer("$S", dtoAnn.name()).build());
        builder.addMethod(MethodSpec.methodBuilder("getDtoName").addStatement("return $L", DTO_NAME_FIELD_NAME)
                .addModifiers(Modifier.PUBLIC).returns(String.class).build());
    }

    private void appendInterface(TypeSpec.Builder builder, Dto dtoAnn) {
        switch (dtoAnn.type()) {
        case CREATE:
            builder.addSuperinterface(RemoteCreateDto.class);
            break;
        case MERGE:
            builder.addSuperinterface(RemoteMergeDto.class);
            break;
        case BOTH:
            builder.addSuperinterface(RemoteMergeDto.class);
            builder.addSuperinterface(RemoteCreateDto.class);
            break;
        }
    }

    private void appendFields(final TypeSpec.Builder builder, Class<?> type) {
        ReflectionUtils.doWithFields(type, new FieldCallback() {
            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                if (typeResolver.supports(field.getType())) {
                    builder.addField(typeResolver.resolve(field.getType()), field.getName(), Modifier.PRIVATE);
                } else {
                    builder.addField(field.getType(), field.getName(), Modifier.PRIVATE);
                }
            }
        }, fieldFilter);

    }

    @Override
    public void setConfiguration(Configuration configuration) {
        this.namingStrategy = configuration.get("defaultNamingStrategy");
        this.typeFileWriter = configuration.get("typeFileWriter");
        this.typeResolver = configuration.get("defaultTypeResolver");
    }

}
