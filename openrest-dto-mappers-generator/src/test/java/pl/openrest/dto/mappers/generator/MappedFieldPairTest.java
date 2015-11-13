package pl.openrest.dto.mappers.generator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MappedFieldPairTest {

    @Mock
    private MappedFieldInformation dtoFieldInfo;
    @Mock
    private MappedFieldInformation entityFieldInfo;

    @Test(expected = IllegalArgumentException.class)
    public void shouldConstructorThrowExceptionOnNonMatchingDtoAndEntityFieldTypesIfNonNestedDto() throws Exception {
        // given
        Mockito.doReturn(Object.class).when(dtoFieldInfo).getType();
        Mockito.doReturn(Long.class).when(entityFieldInfo).getType();
        Mockito.when(dtoFieldInfo.isDto()).thenReturn(false);
        // when
        new MappedFieldPair(dtoFieldInfo, entityFieldInfo);
        // then
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldConstructorThrowExceptionOnNonMatchingDtoAndEntityFieldTypesIfNestedDtoHasEntityTypeDifferentThanEntityFieldType()
            throws Exception {
        // given
        Mockito.doReturn(Object.class).when(dtoFieldInfo).getType();
        Mockito.doReturn(Long.class).when(entityFieldInfo).getType();
        Mockito.when(dtoFieldInfo.isDto()).thenReturn(true);
        Mockito.doReturn(Long.class).when(dtoFieldInfo).getEntityType();
        // when
        new MappedFieldPair(dtoFieldInfo, entityFieldInfo);
        // then
    }

}
