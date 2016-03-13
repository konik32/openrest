package pl.openrest.dto.mappers.generator;

import pl.openrest.dto.mappers.generator.utils.CodeBlockUtils;

import com.squareup.javapoet.CodeBlock;

public class MappedFieldPair {

	private static final String NON_MATCHING_TYPE_ERROR_MESSAGE_FORMAT = "%s and %s should have the same type or dto field type should be a dto itself";
	private static final String NON_MATCHING_NAME_ERROR_MESSAGE_FORMAT = "Names of fields %s and %s do not match";
	protected final MappedFieldInformation dtoFieldInfo;
	protected final MappedFieldInformation entityFieldInfo;

	public MappedFieldPair(MappedFieldInformation dtoFieldInfo,
			MappedFieldInformation entityFieldInfo) {
		this.dtoFieldInfo = dtoFieldInfo;
		this.entityFieldInfo = entityFieldInfo;
		verifyType();
		verifyName();
	}

	protected void verifyType() {
		if (!dtoFieldInfo.getType().equals(entityFieldInfo.getType())
				&& (!dtoFieldInfo.isDto() || !dtoFieldInfo.getEntityType()
						.equals(entityFieldInfo.getType())))
			throw new IllegalArgumentException(String.format(
					NON_MATCHING_TYPE_ERROR_MESSAGE_FORMAT,
					dtoFieldInfo.toString(), entityFieldInfo.toString()));
	}

	protected void verifyName() {
		if (!dtoFieldInfo.getName().equals(entityFieldInfo.getName()))
			throw new IllegalArgumentException(String.format(
					NON_MATCHING_NAME_ERROR_MESSAGE_FORMAT,
					dtoFieldInfo.toString(), entityFieldInfo.toString()));
	}

	public CodeBlock toCreateCodeBlock() {
		if (dtoFieldInfo.isDto()) {
			CodeBlock getterCodeBlock = CodeBlockUtils.delegateCreateCodeBlock(
					entityFieldInfo.getType(), CodeBlockUtils
							.getterCodeBlock(dtoFieldInfo.getGetterName()));
			return CodeBlockUtils.setterCodeBlock(
					entityFieldInfo.getSetterName(), getterCodeBlock);
		}
		return CodeBlockUtils.setterCodeBlock(entityFieldInfo.getSetterName(),
				CodeBlockUtils.getterCodeBlock(dtoFieldInfo.getGetterName()));
	}

	public CodeBlock toUpdateCodeBlock() {
		CodeBlock dtoFieldGetterCodeBlock = CodeBlockUtils
				.getterCodeBlock(dtoFieldInfo.getGetterName());
		CodeBlock innerCodeBlock;
		if (dtoFieldInfo.isDto()) {
			innerCodeBlock = CodeBlockUtils.delegateUpdateCodeBlock(
					dtoFieldGetterCodeBlock, CodeBlockUtils.getterCodeBlock(
							CodeBlockUtils.ENTITY_PARAM_NAME,
							entityFieldInfo.getGetterName()));
		} else {
			innerCodeBlock = CodeBlockUtils.setterCodeBlock(
					entityFieldInfo.getSetterName(), dtoFieldGetterCodeBlock);
		}
		if (dtoFieldInfo.isPrimitive())
			return innerCodeBlock;
		if (dtoFieldInfo.isNullable()) {
			return CodeBlockUtils.wrapWithNotNullOrNullableIf(
					dtoFieldGetterCodeBlock, CodeBlockUtils
							.getterCodeBlock(dtoFieldInfo
									.getNullableGetterName()), innerCodeBlock);
		} else {
			return CodeBlockUtils.wrapWithNotNullIf(dtoFieldGetterCodeBlock,
					innerCodeBlock);
		}
	}
}
