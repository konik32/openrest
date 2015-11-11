package pl.openrest.dto.mappers.generator.utils;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;

import com.squareup.javapoet.CodeBlock;

public class CodeBlockUtilsTest {

    @Test
    public void shouldSetterCodeBlockReturnCodeBlockForSingleNonDtoField() throws Exception {
        // given
        // when
        CodeBlock codeBlock = CodeBlockUtils.setterCodeBlock("setObject", CodeBlockUtils.getterLiteral("getObject"));
        // then
        Assert.assertEquals("entity.setObject(dto.getObject());\n", codeBlock.toString());
    }

    @Test
    public void shouldSetterCodeBlockReturnCodeBlockWithDelegatedCreate() throws Exception {
        // given
        String delegateLiteral = CodeBlockUtils.delegateCreateLiteral(CodeBlockUtils.getterLiteral("getObject"));
        // when
        CodeBlock codeBlock = CodeBlockUtils.setterCodeBlock("setObject", delegateLiteral);
        // then
        Assert.assertEquals("entity.setObject(mapperDelegator.create(dto.getObject()));\n", codeBlock.toString());
    }

    @Test
    public void shouldCollectionSetterCodeBlockReturnCodeBlockForNonDtoCollectionField() throws Exception {
        // given
        // when
        CodeBlock codeBlock = CodeBlockUtils
                .collectionSetterCodeBlock("objects", "setObjects", "getObjects", ArrayList.class, Object.class);
        // then
        Assert.assertEquals("java.util.ArrayList<java.lang.Object> objects = new java.util.ArrayList<java.lang.Object>();\n"
                + "for(java.lang.Object o: dto.getObjects()) {\n" + "  objects.add(o);\n" + "}\n" + "entity.setObjects(objects);\n",
                codeBlock.toString());
    }

    @Test
    public void shouldDtoCollectionSetterCodeBlockReturnCodeBlockWithDelegatedCreate() throws Exception {
        // given
        // when
        CodeBlock codeBlock = CodeBlockUtils.dtoCollectionSetterCodeBlock("objects", "setObjects", "getObjects", ArrayList.class,
                Object.class, Long.class);
        // then
        Assert.assertEquals("java.util.ArrayList<java.lang.Object> objects = new java.util.ArrayList<java.lang.Object>();\n"
                + "for(java.lang.Long o: dto.getObjects()) {\n" + "  if(o != null) {\n"
                + "    objects.add(mapperDelegator.create(o));\n  }\n" + "}\n" + "entity.setObjects(objects);\n", codeBlock.toString());
    }

}
