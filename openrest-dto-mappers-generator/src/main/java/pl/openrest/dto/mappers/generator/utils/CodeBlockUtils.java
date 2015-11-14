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

    public static String delegateCreateLiteral(String getterLiteral) {
        return String.format("%s.create(%s)", MAPPER_DELEGATOR_FIELD_NAME, getterLiteral);
    }

    public static CodeBlock delegateUpdateCodeBlock(String dtoGetterLiteral, String entityGetterLiteral) {
        return CodeBlock.builder().addStatement("$L.update($L,$L)", MAPPER_DELEGATOR_FIELD_NAME, dtoGetterLiteral, entityGetterLiteral)
                .build();
    }

    public static CodeBlock setterCodeBlock(String setter, String getterLiteral) {
        return CodeBlock.builder().addStatement("$L.$L($L)", ENTITY_PARAM_NAME, setter, getterLiteral).build();
    }

    public static CodeBlock addCodeBlock(String collectionName, String addLiteral) {
        return CodeBlock.builder().addStatement("$L.add($L)", collectionName, addLiteral).build();
    }

    public static CodeBlock collectionVariable(String collectionName, Class<?> collectionType, Class<?> rawType) {
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
                .addStatement("this.$L = $L", MAPPER_DELEGATOR_FIELD_NAME, MAPPER_DELEGATOR_FIELD_NAME).build();
    }

    public static CodeBlock collecionLoop(Class<?> elementType, String getterLiteral, CodeBlock codeBlock) {
        return CodeBlock.builder().beginControlFlow("for($T $L: $L)", elementType, COLLECTION_ELEM_NAME, getterLiteral).add(codeBlock)
                .endControlFlow().build();
    }

    public static CodeBlock wrapWithNotNullOrNullableIf(String getterLiteral, String nullableGetterLiteral, CodeBlock codeBlock) {
        return CodeBlock.builder().beginControlFlow("if($L != null || $L)", getterLiteral, nullableGetterLiteral).add(codeBlock)
                .endControlFlow().build();
    }

    public static CodeBlock wrapWithNullableIf(String nullableGetterLiteral, CodeBlock codeBlock) {
        return CodeBlock.builder().beginControlFlow("if($L)", nullableGetterLiteral).add(codeBlock).endControlFlow().build();
    }

    public static CodeBlock wrapWithNotNullIf(String getterLiteral, CodeBlock codeBlock) {
        return CodeBlock.builder().beginControlFlow("if($L != null)", getterLiteral).add(codeBlock).endControlFlow().build();
    }

    public static CodeBlock wrapWithElse(CodeBlock codeBlock) {
        return CodeBlock.builder().beginControlFlow("else").add(codeBlock).endControlFlow().build();
    }

    public static CodeBlock concatenate(CodeBlock... blocks) {
        CodeBlock.Builder builder = CodeBlock.builder();
        for (CodeBlock block : blocks) {
            builder.add(block);
        }
        return builder.build();
    }

    public static String getterLiteral(String property, String getter) {
        return String.format("%s.%s()", property, getter);
    }

    public static String getterLiteral(String getter) {
        return getterLiteral(DTO_PARAM_NAME, getter);
    }

}
