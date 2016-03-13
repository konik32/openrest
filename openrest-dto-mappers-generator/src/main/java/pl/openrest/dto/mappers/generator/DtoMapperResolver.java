package pl.openrest.dto.mappers.generator;

import java.lang.reflect.Field;
import java.util.Set;

import javax.annotation.Generated;
import javax.lang.model.element.Modifier;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;
import org.springframework.util.ReflectionUtils.FieldFilter;

import pl.openrest.dto.annotations.Dto;
import pl.openrest.dto.mapper.CreateMapper;
import pl.openrest.dto.mapper.Default;
import pl.openrest.dto.mapper.UpdateMapper;
import pl.openrest.dto.mappers.generator.utils.CodeBlockUtils;
import pl.openrest.generator.commons.Configuration;
import pl.openrest.generator.commons.ConfigurationAware;
import pl.openrest.generator.commons.RemoteClassNamingStrategy;
import pl.openrest.generator.commons.type.TypeFileWriter;
import pl.openrest.generator.commons.type.TypeResolver;

import com.google.common.base.Predicate;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

public class DtoMapperResolver implements TypeResolver, ConfigurationAware {
    private static final String GENERATOR_NAME = "pl.openrest.dto.mappers.generator";

    private RemoteClassNamingStrategy namingStrategy;
    private TypeFileWriter typeFileWriter;

    private final FieldFilter fieldFilter;

    public DtoMapperResolver(FieldFilter fieldFilter) {
        this.fieldFilter = fieldFilter;
    }

    @Override
    public boolean supports(Class<?> type) {
        return AnnotationUtils.getAnnotation(type, Dto.class) != null;
    }

    @Override
    public TypeName resolve(Class<?> dtoType) {
        String className = namingStrategy.getClassName(dtoType.getSimpleName());
        String packageName = namingStrategy.getPackageName(dtoType.getPackage().getName());
        Class<?> entityType = dtoType.getAnnotation(Dto.class).entityType();

        TypeSpec.Builder typeBuilder = TypeSpec.classBuilder(className).addModifiers(Modifier.PUBLIC)
                .addSuperinterface(ParameterizedTypeName.get(CreateMapper.class, entityType, dtoType))
                .addSuperinterface(ParameterizedTypeName.get(UpdateMapper.class, entityType, dtoType))
                .addAnnotation(AnnotationSpec.builder(Generated.class).addMember("value", "$S", GENERATOR_NAME).build())
                .addAnnotation(AnnotationSpec.builder(Component.class).build())
                .addAnnotation(AnnotationSpec.builder(Default.class).build());
        //
        MethodSpec.Builder createMethod = getCreateMethodDefinition(dtoType, entityType);
        MethodSpec.Builder mergeMethod = getMergeMethodDefinition(dtoType, entityType);
        //
        if (addMapperDelegatorField(dtoType)) {
            typeBuilder.addField(CodeBlockUtils.mapperDelegatorField());
            typeBuilder.addMethod(CodeBlockUtils.constructor());
        }
        //
        appendFields(createMethod, mergeMethod, dtoType, entityType);
        createMethod.addCode(CodeBlockUtils.entityReturnStatement());
        typeBuilder.addMethod(createMethod.build());
        typeBuilder.addMethod(mergeMethod.build());

        typeFileWriter.write(typeBuilder.build(), packageName);
        return ClassName.get(packageName, className);
    }

    private MethodSpec.Builder getCreateMethodDefinition(Class<?> dtoType, Class<?> entityType) {
        return MethodSpec.methodBuilder("create").addParameter(ParameterSpec.builder(dtoType, CodeBlockUtils.DTO_PARAM_NAME).build())
                .addModifiers(Modifier.PUBLIC).returns(entityType).addCode(CodeBlockUtils.entityVariable(entityType));
    }

    private MethodSpec.Builder getMergeMethodDefinition(Class<?> dtoType, Class<?> entityType) {
        return MethodSpec.methodBuilder("merge").addParameter(ParameterSpec.builder(dtoType, CodeBlockUtils.DTO_PARAM_NAME).build())
                .addParameter(ParameterSpec.builder(entityType, CodeBlockUtils.ENTITY_PARAM_NAME).build()).addModifiers(Modifier.PUBLIC);
    }

    private void appendFields(final MethodSpec.Builder createMethod, final MethodSpec.Builder updateMethod, final Class<?> dtoType,
            final Class<?> entityType) {
        ReflectionUtils.doWithFields(dtoType, new FieldCallback() {
            @Override
            public void doWith(Field dtoField) throws IllegalArgumentException, IllegalAccessException {
                Field entityField = ReflectionUtils.findField(entityType, dtoField.getName());
                if (entityField == null)
                    return;
                MappedFieldPair pair = MappedFieldPairFactory.create(dtoField, entityField);
                createMethod.addCode(pair.toCreateCodeBlock());
                updateMethod.addCode(pair.toUpdateCodeBlock());
            }
        }, fieldFilter);

    }

    @SuppressWarnings("unchecked")
    private boolean addMapperDelegatorField(Class<?> dtoType) {
        Set<Field> nestedDtos = org.reflections.ReflectionUtils.getAllFields(dtoType, new Predicate<Field>() {
            @Override
            public boolean apply(Field input) {
                return AnnotationUtils.getAnnotation(input.getType(), Dto.class) != null;
            }
        });
        return !nestedDtos.isEmpty();
    }

    @Override
    public void setConfiguration(Configuration configuration) {
        this.namingStrategy = configuration.get("defaultNamingStrategy");
        this.typeFileWriter = configuration.get("typeFileWriter");
    }

}
