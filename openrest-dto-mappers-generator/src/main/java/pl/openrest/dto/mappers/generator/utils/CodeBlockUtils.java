package pl.openrest.dto.mappers.generator.utils;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterizedTypeName;

public class CodeBlockUtils {

    private static final String DTO_PARAM_NAME = "dto";
    private static final String ENTITY_PARAM_NAME = "entity";
    private static final String MAPPER_DELEGATOR_FIELD_NAME = "mapperDelegator";

    public static String delegateCreateLiteral(String getterLiteral) {
        return String.format("%s.create(%s)", MAPPER_DELEGATOR_FIELD_NAME, getterLiteral);
    }

    public static CodeBlock setterCodeBlock(String setter, String getterLiteral) {
        return CodeBlock.builder().addStatement("$L.$L($L)", ENTITY_PARAM_NAME, setter, getterLiteral).build();
    }

    public static CodeBlock collectionSetterCodeBlock(String name, String setter, String getter, Class<?> collectionType, Class<?> rawType) {
        CodeBlock collectionVariable = collectionVariable(name, collectionType, rawType);
        CodeBlock addCodeBlock = addCodeBlock(name, "o");
        return CodeBlock.builder().add(collectionVariable).beginControlFlow("for($T o: $L)", rawType, getterLiteral(getter))
                .add(addCodeBlock).endControlFlow().add(setterCodeBlock(setter, name)).build();
    }

    public static CodeBlock dtoCollectionSetterCodeBlock(String name, String setter, String getter, Class<?> collectionType,
            Class<?> rawType, Class<?> rawDtoType) {
        CodeBlock collectionVariable = collectionVariable(name, collectionType, rawType);
        CodeBlock addCodeBlock = addCodeBlock(name, delegateCreateLiteral("o"));
        return CodeBlock.builder().add(collectionVariable).beginControlFlow("for($T o: $L)", rawDtoType, getterLiteral(getter))
                .add(wrapWithNotNullIf("o", addCodeBlock)).endControlFlow().add(setterCodeBlock(setter, name)).build();
    }

    public static CodeBlock wrapWithNotNullIf(String getterLiteral, CodeBlock codeBlock) {
        return CodeBlock.builder().beginControlFlow("if($L != null)", getterLiteral).add(codeBlock).endControlFlow().build();
    }

    public static String getterLiteral(String property, String getter) {
        return String.format("%s.%s()", property, getter);
    }

    public static String getterLiteral(String getter) {
        return getterLiteral(DTO_PARAM_NAME, getter);
    }

    private static CodeBlock addCodeBlock(String collectionName, String addLiteral) {
        return CodeBlock.builder().addStatement("$L.add($L)", collectionName, addLiteral).build();
    }

    private static CodeBlock collectionVariable(String collectionName, Class<?> collectionType, Class<?> rawType) {
        ParameterizedTypeName collectionTypeName = ParameterizedTypeName.get(collectionType, rawType);
        return CodeBlock.builder().addStatement("$T $L = new $T()", collectionTypeName, collectionName, collectionTypeName).build();
    }

}
