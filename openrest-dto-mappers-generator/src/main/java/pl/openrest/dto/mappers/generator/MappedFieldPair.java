package pl.openrest.dto.mappers.generator;

public class MappedFieldPair {

    private static final String NON_MATCHING_TYPE_ERROR_MESSAGE_FORMAT = "%s and %s should have the same type or dto field type should be a dto itself";

    protected final MappedFieldInformation dtoFieldInfo;
    protected final MappedFieldInformation entityFieldInfo;

    public MappedFieldPair(MappedFieldInformation dtoFieldInfo, MappedFieldInformation entityFieldInfo) {
        this.dtoFieldInfo = dtoFieldInfo;
        this.entityFieldInfo = entityFieldInfo;
        verifyType();
    }

    protected void verifyType() {
        if (!dtoFieldInfo.getType().equals(entityFieldInfo.getType())
                && (!dtoFieldInfo.isDto() || dtoFieldInfo.getEntityType().equals(entityFieldInfo.getType())))
            throw new IllegalArgumentException(String.format(NON_MATCHING_TYPE_ERROR_MESSAGE_FORMAT, dtoFieldInfo.toString(),
                    entityFieldInfo.toString()));
    }
}
