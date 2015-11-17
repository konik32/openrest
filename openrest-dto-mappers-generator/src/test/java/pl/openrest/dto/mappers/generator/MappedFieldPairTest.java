package pl.openrest.dto.mappers.generator;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.squareup.javapoet.CodeBlock;

@RunWith(MockitoJUnitRunner.class)
public class MappedFieldPairTest {

    @Mock
    private MappedFieldInformation dtoFieldInfo;
    @Mock
    private MappedFieldInformation entityFieldInfo;

    private MappedFieldPair fieldPair;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() {
        Mockito.doReturn(Object.class).when(dtoFieldInfo).getType();
        Mockito.doReturn(Object.class).when(entityFieldInfo).getType();

        Mockito.doReturn(Long.class).when(dtoFieldInfo).getDeclaringClass();
        Mockito.doReturn(Integer.class).when(entityFieldInfo).getDeclaringClass();

        Mockito.when(dtoFieldInfo.getGetterName()).thenReturn("getName");
        Mockito.when(entityFieldInfo.getGetterName()).thenReturn("getName");
        Mockito.when(entityFieldInfo.getSetterName()).thenReturn("setName");

        Mockito.when(dtoFieldInfo.getName()).thenReturn("name");
        Mockito.when(entityFieldInfo.getName()).thenReturn("name");

        Mockito.when(dtoFieldInfo.toString()).thenReturn("dtoField");
        Mockito.when(entityFieldInfo.toString()).thenReturn("entityField");

        Mockito.when(dtoFieldInfo.isDto()).thenReturn(false);
        Mockito.when(dtoFieldInfo.isNullable()).thenReturn(false);
        fieldPair = new MappedFieldPair(dtoFieldInfo, entityFieldInfo);
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
        Mockito.doReturn(Object.class).when(dtoFieldInfo).getEntityType();

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(Matchers
                .equalTo("dtoField and entityField should have the same type or dto field type should be a dto itself"));
        // when
        new MappedFieldPair(dtoFieldInfo, entityFieldInfo);
        // then
    }

    @Test
    public void shouldConstructorThrowExceptionOnNonMatchingFieldNames() throws Exception {
        // given
        Mockito.when(dtoFieldInfo.getName()).thenReturn("name");
        Mockito.when(entityFieldInfo.getName()).thenReturn("year");

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(Matchers.equalTo("Names of fields dtoField and entityField do not match"));
        // when
        new MappedFieldPair(dtoFieldInfo, entityFieldInfo);
        // then
    }

    @Test
    public void shouldToCreateCodeBlockReturnCodeBlockWithSimpleGetterAndSetterForNonDtoField() throws Exception {
        // given
        // when
        CodeBlock codeBlock = fieldPair.toCreateCodeBlock();
        // then
        Assert.assertEquals("entity.setName(dto.getName());\n", codeBlock.toString());
    }

    @Test
    public void shouldToCreateCodeBlockReturnCodeBlockWithMapperDelegatorOnIsDtoTrue() throws Exception {
        // given
        Mockito.when(dtoFieldInfo.isDto()).thenReturn(true);
        // when
        CodeBlock codeBlock = fieldPair.toCreateCodeBlock();
        // then
        Assert.assertEquals("entity.setName((java.lang.Object) mapperDelegator.create(dto.getName()));\n", codeBlock.toString());
    }

    @Test
    public void shouldToUpdateCodeBlockReturnCodeBlockWithSimpleGetterAndSetterForNonDtoField() throws Exception {
        // given
        // when
        CodeBlock codeBlock = fieldPair.toUpdateCodeBlock();
        // then
        Assert.assertEquals("if(dto.getName()!=null){entity.setName(dto.getName());}", codeBlock.toString().replaceAll("\\s", ""));
    }

    @Test
    public void shouldToUpdateCodeBlockReturnCodeBlockWithMapperDelegatorOnIsDtoTrue() throws Exception {
        // given
        Mockito.when(dtoFieldInfo.isDto()).thenReturn(true);
        // Mockito.when(dtoFieldInfo.isNullable()).thenReturn(true);
        // Mockito.when(dtoFieldInfo.getNullableGetterName()).thenReturn("isSetName");
        // when
        CodeBlock codeBlock = fieldPair.toUpdateCodeBlock();
        // then
        Assert.assertEquals("if(dto.getName()!=null){mapperDelegator.merge(dto.getName(),entity.getName());}", codeBlock.toString()
                .replaceAll("\\s", ""));
    }

    @Test
    public void shouldToUpdateCodeBlockReturnCodeBlockWithMapperDelegatorOnIsDtoTrueAndNullableTrue() throws Exception {
        // given
        Mockito.when(dtoFieldInfo.isDto()).thenReturn(true);
        Mockito.when(dtoFieldInfo.isNullable()).thenReturn(true);
        Mockito.when(dtoFieldInfo.getNullableGetterName()).thenReturn("isSetName");
        // when
        CodeBlock codeBlock = fieldPair.toUpdateCodeBlock();
        // then
        Assert.assertEquals("if(dto.getName()!=null||dto.isSetName()){mapperDelegator.merge(dto.getName(),entity.getName());}", codeBlock
                .toString().replaceAll("\\s", ""));
    }

}
