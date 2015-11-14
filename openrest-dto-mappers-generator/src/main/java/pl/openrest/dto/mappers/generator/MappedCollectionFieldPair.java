package pl.openrest.dto.mappers.generator;

import pl.openrest.dto.mappers.generator.utils.CodeBlockUtils;

import com.squareup.javapoet.CodeBlock;

public class MappedCollectionFieldPair extends MappedFieldPair {

    public MappedCollectionFieldPair(MappedCollectionFieldInformation dtoFieldInfo, MappedCollectionFieldInformation entityFieldInfo) {
        super(dtoFieldInfo, entityFieldInfo);
    }

    @Override
    public CodeBlock toCreateCodeBlock() {
        String dtoFieldGetterLiteral = CodeBlockUtils.getterLiteral(dtoFieldInfo.getGetterName());
        CodeBlock ifBlock = CodeBlockUtils.wrapWithNotNullIf(dtoFieldGetterLiteral, toIfInnerBlock());
        return CodeBlockUtils.concatenate(ifBlock, toElseCodeBlock());
    }

    private CodeBlock toIfInnerBlock() {
        String addLiteral = dtoFieldInfo.isDto() ? CodeBlockUtils.delegateCreateLiteral(CodeBlockUtils.COLLECTION_ELEM_NAME)
                : CodeBlockUtils.COLLECTION_ELEM_NAME;
        String dtoFieldGetterLiteral = CodeBlockUtils.getterLiteral(dtoFieldInfo.getGetterName());
        CodeBlock add = CodeBlockUtils.addCodeBlock(dtoFieldInfo.getName(), addLiteral);
        CodeBlock loop = CodeBlockUtils.collecionLoop(getDtoField().getRawType(), dtoFieldGetterLiteral, add);
        CodeBlock collectionVariable = CodeBlockUtils.collectionVariable(dtoFieldInfo.getName(), getEntityField().getType(),
                getEntityField().getRawType());
        CodeBlock setterNotNull = CodeBlockUtils.setterCodeBlock(entityFieldInfo.getSetterName(), dtoFieldInfo.getName());
        return CodeBlockUtils.concatenate(collectionVariable, loop, setterNotNull);
    }

    private CodeBlock toElseCodeBlock() {
        CodeBlock setterNull = CodeBlockUtils.setterCodeBlock(entityFieldInfo.getSetterName(), "null");
        return CodeBlockUtils.wrapWithElse(setterNull);
    }

    @Override
    public CodeBlock toUpdateCodeBlock() {
        String dtoFieldGetterLiteral = CodeBlockUtils.getterLiteral(dtoFieldInfo.getGetterName());
        CodeBlock ifBlock;
        if (dtoFieldInfo.isNullable()) {
            String dtoFieldNullableGetterLiteral = CodeBlockUtils.getterLiteral(dtoFieldInfo.getNullableGetterName());
            ifBlock = CodeBlockUtils.wrapWithNotNullOrNullableIf(dtoFieldGetterLiteral, dtoFieldNullableGetterLiteral, toIfInnerBlock());
        } else {
            ifBlock = CodeBlockUtils.wrapWithNotNullIf(dtoFieldGetterLiteral, toIfInnerBlock());
        }
        return CodeBlockUtils.concatenate(ifBlock, toElseCodeBlock());
    }

    public MappedCollectionFieldInformation getDtoField() {
        return (MappedCollectionFieldInformation) dtoFieldInfo;
    }

    public MappedCollectionFieldInformation getEntityField() {
        return (MappedCollectionFieldInformation) entityFieldInfo;
    }

}
