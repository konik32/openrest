package pl.openrest.dto.mappers.generator;

import java.util.ArrayList;

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
public class MappedCollectionFieldPairTest {

    @Mock
    private MappedCollectionFieldInformation dtoFieldInfo;
    @Mock
    private MappedCollectionFieldInformation entityFieldInfo;

    private MappedCollectionFieldPair fieldPair;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() {
        Mockito.doReturn(ArrayList.class).when(dtoFieldInfo).getType();
        Mockito.doReturn(ArrayList.class).when(entityFieldInfo).getType();

        Mockito.doReturn(Object.class).when(dtoFieldInfo).getRawType();
        Mockito.doReturn(Object.class).when(entityFieldInfo).getRawType();

        Mockito.doReturn(Long.class).when(dtoFieldInfo).getDeclaringClass();
        Mockito.doReturn(Integer.class).when(entityFieldInfo).getDeclaringClass();

        Mockito.when(dtoFieldInfo.getGetterName()).thenReturn("getList");
        Mockito.when(entityFieldInfo.getGetterName()).thenReturn("getList");
        Mockito.when(entityFieldInfo.getSetterName()).thenReturn("setList");

        Mockito.when(dtoFieldInfo.getName()).thenReturn("list");
        Mockito.when(entityFieldInfo.getName()).thenReturn("list");

        Mockito.when(dtoFieldInfo.toString()).thenReturn("dtoField");
        Mockito.when(entityFieldInfo.toString()).thenReturn("entityField");

        Mockito.when(dtoFieldInfo.isDto()).thenReturn(false);
        Mockito.when(dtoFieldInfo.isNullable()).thenReturn(false);

        fieldPair = new MappedCollectionFieldPair(dtoFieldInfo, entityFieldInfo);
    }

    @Test
    public void shouldToCreateCodeBlockReturnCodeBlockOnIsDtoFalse() throws Exception {
        // given
        // when
        CodeBlock codeBlock = fieldPair.toCreateCodeBlock();
        // then
        String expected = "if(dto.getList() !=null){"//
                + "java.util.ArrayList<java.lang.Object> list = new java.util.ArrayList<java.lang.Object>();"//
                + "for(java.lang.Object o: dto.getList()){"//
                + "list.add(o);"//
                + "}"//
                + "entity.setList(list);"//
                + "}else{"//
                + "entity.setList(null);"//
                + "}";
        Assert.assertEquals(expected.replaceAll("\\s", ""), codeBlock.toString().replaceAll("\\s", ""));
    }

    @Test
    public void shouldToCreateCodeBlockReturnCodeBlockOnIsDtoTrue() throws Exception {
        // given
        Mockito.when(dtoFieldInfo.isDto()).thenReturn(true);
        Mockito.doReturn(Long.class).when(dtoFieldInfo).getRawType();
        // when
        CodeBlock codeBlock = fieldPair.toCreateCodeBlock();
        // then
        String expected = "if(dto.getList() !=null){"//
                + "java.util.ArrayList<java.lang.Object> list = new java.util.ArrayList<java.lang.Object>();"//
                + "for(java.lang.Long o: dto.getList()){"//
                + "list.add((java.lang.Object) mapperDelegator.create(o));"//
                + "}"//
                + "entity.setList(list);"//
                + "}else{"//
                + "entity.setList(null);"//
                + "}";
        Assert.assertEquals(expected.replaceAll("\\s", ""), codeBlock.toString().replaceAll("\\s", ""));
    }

    @Test
    public void shouldToCreateCodeBlockReturnCodeBlockOnIsDtoTrueAndIsNullableTrue() throws Exception {
        // given
        Mockito.when(dtoFieldInfo.isDto()).thenReturn(true);
        Mockito.when(dtoFieldInfo.isNullable()).thenReturn(true);
        Mockito.when(dtoFieldInfo.getNullableGetterName()).thenReturn("isListSet");
        Mockito.doReturn(Long.class).when(dtoFieldInfo).getRawType();
        // when
        CodeBlock codeBlock = fieldPair.toUpdateCodeBlock();
        // then
        String expected = "if(dto.getList() !=null || dto.isListSet()){"//
                + "java.util.ArrayList<java.lang.Object> list = new java.util.ArrayList<java.lang.Object>();"//
                + "for(java.lang.Long o: dto.getList()){"//
                + "list.add((java.lang.Object) mapperDelegator.create(o));"//
                + "}"//
                + "entity.setList(list);"//
                + "}else{"//
                + "entity.setList(null);"//
                + "}";
        Assert.assertEquals(expected.replaceAll("\\s", ""), codeBlock.toString().replaceAll("\\s", ""));
    }

}
