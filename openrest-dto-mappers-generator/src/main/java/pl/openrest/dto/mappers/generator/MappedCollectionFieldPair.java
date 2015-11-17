package pl.openrest.dto.mappers.generator;

import pl.openrest.dto.mappers.generator.utils.CodeBlockUtils;

import com.squareup.javapoet.CodeBlock;

public class MappedCollectionFieldPair extends MappedFieldPair {

    public MappedCollectionFieldPair(MappedCollectionFieldInformation dtoFieldInfo, MappedCollectionFieldInformation entityFieldInfo) {
        super(dtoFieldInfo, entityFieldInfo);
    }

    @Override
    public CodeBlock toCreateCodeBlock() {
        CodeBlock dtoFieldGetterCodeBlock = CodeBlockUtils.getterCodeBlock(dtoFieldInfo.getGetterName());
        CodeBlock ifBlock = CodeBlockUtils.wrapWithNotNullIf(dtoFieldGetterCodeBlock, toIfInnerBlock());
        return CodeBlockUtils.concatenate(ifBlock, toElseCodeBlock());
    }

    private CodeBlock toIfInnerBlock() {
        CodeBlock addCodeBlock = dtoFieldInfo.isDto() ? CodeBlockUtils.delegateCreateCodeBlock(getEntityField().getRawType(),
                CodeBlockUtils.codeBlock(CodeBlockUtils.COLLECTION_ELEM_NAME)) : CodeBlockUtils
                .codeBlock(CodeBlockUtils.COLLECTION_ELEM_NAME);
        CodeBlock dtoFieldGetterCodeBlock = CodeBlockUtils.getterCodeBlock(dtoFieldInfo.getGetterName());
        CodeBlock add = CodeBlockUtils.addCodeBlock(dtoFieldInfo.getName(), addCodeBlock);
        CodeBlock loop = CodeBlockUtils.collecionLoop(getDtoField().getRawType(), dtoFieldGetterCodeBlock, add);
        CodeBlock collectionVariable = CodeBlockUtils.collectionVariable(dtoFieldInfo.getName(), getEntityField().getType(),
                getEntityField().getRawType());
        CodeBlock setterNotNull = CodeBlockUtils.setterCodeBlock(entityFieldInfo.getSetterName(),
                CodeBlockUtils.codeBlock(dtoFieldInfo.getName()));
        return CodeBlockUtils.concatenate(collectionVariable, loop, setterNotNull);
    }

    private CodeBlock toElseCodeBlock() {
        CodeBlock setterNull = CodeBlockUtils.setterCodeBlock(entityFieldInfo.getSetterName(), CodeBlockUtils.codeBlock("null"));
        return CodeBlockUtils.wrapWithElse(setterNull);
    }

    @Override
    public CodeBlock toUpdateCodeBlock() {
        CodeBlock dtoFieldGetterCodeBlock = CodeBlockUtils.getterCodeBlock(dtoFieldInfo.getGetterName());
        CodeBlock ifBlock;
        if (dtoFieldInfo.isNullable()) {
            CodeBlock dtoFieldNullableGetterCodeBlock = CodeBlockUtils.getterCodeBlock(dtoFieldInfo.getNullableGetterName());
            ifBlock = CodeBlockUtils
                    .wrapWithNotNullOrNullableIf(dtoFieldGetterCodeBlock, dtoFieldNullableGetterCodeBlock, toIfInnerBlock());
        } else {
            ifBlock = CodeBlockUtils.wrapWithNotNullIf(dtoFieldGetterCodeBlock, toIfInnerBlock());
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
