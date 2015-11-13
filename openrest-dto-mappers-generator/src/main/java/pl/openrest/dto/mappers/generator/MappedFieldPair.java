package pl.openrest.dto.mappers.generator;

import pl.openrest.dto.mappers.generator.utils.CodeBlockUtils;

import com.squareup.javapoet.CodeBlock;

public class MappedFieldPair {

    private static final String NON_MATCHING_TYPE_ERROR_MESSAGE_FORMAT = "%s and %s should have the same type or dto field type should be a dto itself";
    private static final String NON_MATCHING_NAME_ERROR_MESSAGE_FORMAT = "Names of fields %s and %s do not match";
    protected final MappedFieldInformation dtoFieldInfo;
    protected final MappedFieldInformation entityFieldInfo;

    public MappedFieldPair(MappedFieldInformation dtoFieldInfo, MappedFieldInformation entityFieldInfo) {
        this.dtoFieldInfo = dtoFieldInfo;
        this.entityFieldInfo = entityFieldInfo;
        verifyType();
        verifyName();
    }

    protected void verifyType() {
        if (!dtoFieldInfo.getType().equals(entityFieldInfo.getType())
                && (!dtoFieldInfo.isDto() || dtoFieldInfo.getEntityType().equals(entityFieldInfo.getType())))
            throw new IllegalArgumentException(String.format(NON_MATCHING_TYPE_ERROR_MESSAGE_FORMAT, dtoFieldInfo.toString(),
                    entityFieldInfo.toString()));
    }

    protected void verifyName() {
        if (!dtoFieldInfo.getName().equals(entityFieldInfo.getName()))
            throw new IllegalArgumentException(String.format(NON_MATCHING_NAME_ERROR_MESSAGE_FORMAT, dtoFieldInfo.toString(),
                    entityFieldInfo.toString()));
    }

    public CodeBlock toCreateCodeBlock() {
        if (dtoFieldInfo.isDto()) {
            String getterLiteral = CodeBlockUtils.delegateCreateLiteral(CodeBlockUtils.getterLiteral(dtoFieldInfo.getGetterName()));
            return CodeBlockUtils.setterCodeBlock(entityFieldInfo.getSetterName(), getterLiteral);
        }
        return CodeBlockUtils.setterCodeBlock(entityFieldInfo.getSetterName(), CodeBlockUtils.getterLiteral(dtoFieldInfo.getGetterName()));
    }

    public CodeBlock toUpdateCodeBlock() {
        String dtoFieldGetterLiteral = CodeBlockUtils.getterLiteral(dtoFieldInfo.getGetterName());
        if (dtoFieldInfo.isDto()) {
            CodeBlock innerCodeBlock = CodeBlockUtils.delegateUpdateCodeBlock(dtoFieldGetterLiteral,
                    CodeBlockUtils.getterLiteral(CodeBlockUtils.ENTITY_PARAM_NAME, entityFieldInfo.getGetterName()));
            if (dtoFieldInfo.isNullable()) {
                return CodeBlockUtils.wrapWithNotNullOrNullableIf(dtoFieldGetterLiteral,
                        CodeBlockUtils.getterLiteral(dtoFieldInfo.getNullableGetterName()), innerCodeBlock);
            } else {
                return CodeBlockUtils.wrapWithNotNullIf(dtoFieldGetterLiteral, innerCodeBlock);
            }
        }
        CodeBlock innerCodeBlock = CodeBlockUtils.setterCodeBlock(entityFieldInfo.getSetterName(), dtoFieldGetterLiteral);
        return CodeBlockUtils.wrapWithNotNullIf(dtoFieldGetterLiteral, innerCodeBlock);
    }
}
