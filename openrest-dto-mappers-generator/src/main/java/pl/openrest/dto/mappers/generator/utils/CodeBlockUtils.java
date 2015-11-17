package pl.openrest.dto.mappers.generator.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.lang.model.element.Modifier;

import lombok.NonNull;
import pl.openrest.dto.mapper.MapperDelegator;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;

public class CodeBlockUtils {

    public static final String DTO_PARAM_NAME = "dto";
    public static final String ENTITY_PARAM_NAME = "entity";
    private static final String MAPPER_DELEGATOR_FIELD_NAME = "mapperDelegator";
    public static final String COLLECTION_ELEM_NAME = "o";

    private final static Map<String, Class<? extends Collection>> collectionFallbacks = new HashMap<String, Class<? extends Collection>>();
    static {
        collectionFallbacks.put(Collection.class.getName(), ArrayList.class);
        collectionFallbacks.put(List.class.getName(), ArrayList.class);
        collectionFallbacks.put(Set.class.getName(), HashSet.class);
        collectionFallbacks.put(SortedSet.class.getName(), TreeSet.class);
        collectionFallbacks.put(Queue.class.getName(), LinkedList.class);

        collectionFallbacks.put("java.util.Deque", LinkedList.class);
        collectionFallbacks.put("java.util.NavigableSet", TreeSet.class);
    }

    public static CodeBlock delegateCreateCodeBlock(@NonNull Class<?> entityType, @NonNull CodeBlock getter) {
        return CodeBlock.builder().add("($T) $L.create($L)", entityType, MAPPER_DELEGATOR_FIELD_NAME, getter).build();
    }

    public static CodeBlock delegateUpdateCodeBlock(@NonNull CodeBlock dtoGetterCodeBlock, @NonNull CodeBlock entityGetterCodeBlock) {
        return CodeBlock.builder().addStatement("$L.merge($L,$L)", MAPPER_DELEGATOR_FIELD_NAME, dtoGetterCodeBlock, entityGetterCodeBlock)
                .build();
    }

    public static CodeBlock entityVariable(@NonNull Class<?> entityType) {
        return CodeBlock.builder().addStatement("$T $L = new $T()", entityType, ENTITY_PARAM_NAME, entityType).build();
    }

    public static CodeBlock entityReturnStatement() {
        return CodeBlock.builder().addStatement("return $L", ENTITY_PARAM_NAME).build();
    }

    public static CodeBlock setterCodeBlock(@NonNull String setter, @NonNull CodeBlock getter) {
        return CodeBlock.builder().addStatement("$L.$L($L)", ENTITY_PARAM_NAME, setter, getter).build();
    }

    public static CodeBlock addCodeBlock(@NonNull String collectionName, @NonNull CodeBlock addCodeBlock) {
        return CodeBlock.builder().addStatement("$L.add($L)", collectionName, addCodeBlock).build();
    }

    public static CodeBlock collectionVariable(@NonNull String collectionName, @NonNull Class<?> collectionType, @NonNull Class<?> rawType) {
        ParameterizedTypeName collectionTypeName = ParameterizedTypeName.get(
                collectionFallbacks.containsKey(collectionType.getName()) ? collectionFallbacks.get(collectionType.getName())
                        : collectionType, rawType);
        return CodeBlock.builder().addStatement("$T $L = new $T()", collectionTypeName, collectionName, collectionTypeName).build();
    }

    public static FieldSpec mapperDelegatorField() {
        return FieldSpec.builder(MapperDelegator.class, MAPPER_DELEGATOR_FIELD_NAME, Modifier.PRIVATE, Modifier.FINAL).build();
    }

    public static MethodSpec constructor() {
        return MethodSpec.constructorBuilder().addParameter(MapperDelegator.class, MAPPER_DELEGATOR_FIELD_NAME)
                .addStatement("this.$L = $L", MAPPER_DELEGATOR_FIELD_NAME, MAPPER_DELEGATOR_FIELD_NAME).addModifiers(Modifier.PUBLIC)
                .build();
    }

    public static CodeBlock collecionLoop(@NonNull Class<?> elementType, @NonNull CodeBlock getterCodeBlock, @NonNull CodeBlock codeBlock) {
        return CodeBlock.builder().beginControlFlow("for($T $L: $L)", elementType, COLLECTION_ELEM_NAME, getterCodeBlock).add(codeBlock)
                .endControlFlow().build();
    }

    public static CodeBlock wrapWithNotNullOrNullableIf(@NonNull CodeBlock getterCodeBlock, @NonNull CodeBlock nullableGetterCodeBlock,
            @NonNull CodeBlock codeBlock) {
        return CodeBlock.builder().beginControlFlow("if($L != null || $L)", getterCodeBlock, nullableGetterCodeBlock).add(codeBlock)
                .endControlFlow().build();
    }

    public static CodeBlock wrapWithNullableIf(@NonNull CodeBlock nullableGetterCodeBlock, @NonNull CodeBlock codeBlock) {
        return CodeBlock.builder().beginControlFlow("if($L)", nullableGetterCodeBlock).add(codeBlock).endControlFlow().build();
    }

    public static CodeBlock wrapWithNotNullIf(@NonNull CodeBlock getterCodeBlock, @NonNull CodeBlock codeBlock) {
        return CodeBlock.builder().beginControlFlow("if($L != null)", getterCodeBlock).add(codeBlock).endControlFlow().build();
    }

    public static CodeBlock wrapWithElse(@NonNull CodeBlock codeBlock) {
        return CodeBlock.builder().beginControlFlow("else").add(codeBlock).endControlFlow().build();
    }

    public static CodeBlock concatenate(@NonNull CodeBlock... blocks) {
        CodeBlock.Builder builder = CodeBlock.builder();
        for (CodeBlock block : blocks) {
            builder.add(block);
        }
        return builder.build();
    }

    public static CodeBlock getterCodeBlock(@NonNull String property, @NonNull String getter) {
        return CodeBlock.builder().add("$L.$L()", property, getter).build();
    }

    public static CodeBlock getterCodeBlock(@NonNull String getter) {
        return getterCodeBlock(DTO_PARAM_NAME, getter);
    }

    public static CodeBlock codeBlock(String code) {
        return CodeBlock.builder().add(code).build();
    }

}
