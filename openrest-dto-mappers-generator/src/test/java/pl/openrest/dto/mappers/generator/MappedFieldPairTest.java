package pl.openrest.dto.mappers.generator;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
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

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() {
        Mockito.doReturn(Object.class).when(dtoFieldInfo).getType();
        Mockito.doReturn(Object.class).when(entityFieldInfo).getType();

        Mockito.doReturn(Long.class).when(dtoFieldInfo).getDeclaringClass();
        Mockito.doReturn(Integer.class).when(entityFieldInfo).getDeclaringClass();

        Mockito.when(dtoFieldInfo.getGetterName()).thenReturn("getName");
        Mockito.when(entityFieldInfo.getSetterName()).thenReturn("setName");

        Mockito.when(dtoFieldInfo.getName()).thenReturn("name");
        Mockito.when(entityFieldInfo.getName()).thenReturn("name");

        Mockito.when(dtoFieldInfo.toString()).thenReturn("dtoField");
        Mockito.when(entityFieldInfo.toString()).thenReturn("entityField");
    }

    @Test
    public void shouldConstructorThrowExceptionOnNonMatchingDtoAndEntityFieldTypesIfNonNestedDto() throws Exception {
        // given
        Mockito.doReturn(Object.class).when(dtoFieldInfo).getType();
        Mockito.doReturn(Long.class).when(entityFieldInfo).getType();
        Mockito.when(dtoFieldInfo.isDto()).thenReturn(false);

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(Matchers
                .equalTo("dtoField and entityField should have the same type or dto field type should be a dto itself"));
        // when
        new MappedFieldPair(dtoFieldInfo, entityFieldInfo);
        // then
    }

    @Test
    public void shouldConstructorThrowExceptionOnNonMatchingDtoAndEntityFieldTypesIfNestedDtoHasEntityTypeDifferentThanEntityFieldType()
            throws Exception {
        // given
        Mockito.doReturn(Object.class).when(dtoFieldInfo).getType();
        Mockito.doReturn(Long.class).when(entityFieldInfo).getType();
        Mockito.when(dtoFieldInfo.isDto()).thenReturn(true);
        Mockito.doReturn(Long.class).when(dtoFieldInfo).getEntityType();

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(Matchers
                .equalTo("dtoField and entityField should have the same type or dto field type should be a dto itself"));
        // when
        new MappedFieldPair(dtoFieldInfo, entityFieldInfo);
        // then
    }


}
