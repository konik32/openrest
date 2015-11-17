package pl.openrest.dto.mappers.generator.utils;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Modifier;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import pl.openrest.dto.mapper.MapperDelegator;
import pl.openrest.generator.commons.utils.JavaPoetTestUtils;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;

public class CodeBlockUtilsTest {

    @Test
    public void shouldSetterCodeBlockReturnCodeBlockForSingleNonDtoField() throws Exception {
        // given
        // when
        CodeBlock codeBlock = CodeBlockUtils.setterCodeBlock("setObject", CodeBlockUtils.getterCodeBlock("getObject"));
        // then
        Assert.assertEquals("entity.setObject(dto.getObject());\n", codeBlock.toString());
    }

    @Test
    public void shouldSetterCodeBlockReturnCodeBlockWithDelegatedCreate() throws Exception {
        // given
        CodeBlock delegateCreateCodeBlock = CodeBlockUtils.delegateCreateCodeBlock(Object.class,
                CodeBlockUtils.getterCodeBlock("getObject"));
        // when
        CodeBlock codeBlock = CodeBlockUtils.setterCodeBlock("setObject", delegateCreateCodeBlock);
        // then
        Assert.assertEquals("entity.setObject((java.lang.Object) mapperDelegator.create(dto.getObject()));\n", codeBlock.toString());
    }

    @Test
    public void shouldCollectionLoopReturnCodeBlockWithForLoopWithTypeAndGetterLiteral() throws Exception {
        // given
        // when
        CodeBlock codeBlock = CodeBlockUtils.collecionLoop(Object.class, JavaPoetTestUtils.geCodeBlock("dto.getObjects()"), CodeBlock
                .builder().build());
        // then
        Assert.assertThat(codeBlock.toString(), Matchers.containsString("for(java.lang.Object o: dto.getObjects())"));
    }

    @Test
    public void shouldWrapWithNotNullOrNullableIfReturnCodeBlockWithIfWithGetterLiteralAndNullableGetterLiteral() throws Exception {
        // given
        // when
        CodeBlock codeBlock = CodeBlockUtils.wrapWithNotNullOrNullableIf(JavaPoetTestUtils.geCodeBlock("dto.getObject()"),
                JavaPoetTestUtils.geCodeBlock("dto.isObjectSet()"), CodeBlock.builder().build());
        // then
        Assert.assertThat(codeBlock.toString(), Matchers.containsString("if(dto.getObject() != null || dto.isObjectSet())"));
    }

    @Test
    public void shouldWrapWithNotNullIfReturnCodeBlockWithIfWithGetterLiteral() throws Exception {
        // given
        // when
        CodeBlock codeBlock = CodeBlockUtils.wrapWithNotNullIf(JavaPoetTestUtils.geCodeBlock("dto.getObject()"), CodeBlock.builder()
                .build());
        // then
        Assert.assertThat(codeBlock.toString(), Matchers.containsString("if(dto.getObject() != null)"));
    }

    @Test
    public void shouldWrapWithNullableIfReturnCodeBlockWithIfWithNullableGetterLiteral() throws Exception {
        // given
        // when
        CodeBlock codeBlock = CodeBlockUtils.wrapWithNullableIf(JavaPoetTestUtils.geCodeBlock("dto.isObjectSet()"), CodeBlock.builder()
                .build());
        // then
        Assert.assertThat(codeBlock.toString(), Matchers.containsString("if(dto.isObjectSet())"));
    }

    @Test
    public void shouldWrapWithNullableIfReturnIfWithWrappedCodeBlock() throws Exception {
        // given
        // when
        CodeBlock codeBlock = CodeBlockUtils.wrapWithNullableIf(JavaPoetTestUtils.geCodeBlock("dto.isObjectSet()"), CodeBlock.builder()
                .add("codeBlock").build());
        // then
        Assert.assertThat(codeBlock.toString().replaceAll("\\s", ""), Matchers.containsString("{codeBlock}"));
    }

    @Test
    public void shouldWrapWithNotNullOrNullableIfReturnIfWithWrappedCodeBlock() throws Exception {
        // given
        // when
        CodeBlock codeBlock = CodeBlockUtils.wrapWithNotNullOrNullableIf(JavaPoetTestUtils.geCodeBlock("dto.getObject()"),
                JavaPoetTestUtils.geCodeBlock("dto.isObjectSet()"), CodeBlock.builder().add("codeBlock").build());
        // then
        Assert.assertThat(codeBlock.toString().replaceAll("\\s", ""), Matchers.containsString("{codeBlock}"));
    }

    @Test
    public void shouldWrapWithNotNullIfReturnIfWithWrappedCodeBlock() throws Exception {
        // given
        // when
        CodeBlock codeBlock = CodeBlockUtils.wrapWithNotNullIf(JavaPoetTestUtils.geCodeBlock("dto.getObjectSet()"), CodeBlock.builder()
                .add("codeBlock").build());
        // then
        Assert.assertThat(codeBlock.toString().replaceAll("\\s", ""), Matchers.containsString("{codeBlock}"));
    }

    @Test
    public void shouldWrapWithElseReturnIfWithWrappedCodeBlock() throws Exception {
        // given
        // when
        CodeBlock codeBlock = CodeBlockUtils.wrapWithElse(CodeBlock.builder().add("codeBlock").build());
        // then
        Assert.assertThat(codeBlock.toString().replaceAll("\\s", ""), Matchers.containsString("else{codeBlock}"));
    }

    @Test
    public void shouldConstructorReturnMethodSpecWithMapperDelegatorParameter() throws Exception {
        // given
        // when
        MethodSpec spec = CodeBlockUtils.constructor();
        // then
        Assert.assertThat(spec.toString(), Matchers.containsString("this.mapperDelegator = mapperDelegator;"));
        Assert.assertEquals(1, spec.parameters.size());
        Assert.assertEquals(TypeName.get(MapperDelegator.class), spec.parameters.get(0).type);
        Assert.assertEquals("mapperDelegator", spec.parameters.get(0).name);
    }

    @Test
    public void shouldConstructorReturnMethodSpecPublicConstructorWithAutowiredAnnotation() throws Exception {
        // given
        // when
        MethodSpec spec = CodeBlockUtils.constructor();
        // then
        Assert.assertThat(spec.modifiers, Matchers.contains(Modifier.PUBLIC));
        Assert.assertEquals(1, spec.annotations.size());
        Assert.assertEquals(TypeName.get(Autowired.class), spec.annotations.get(0).type);
    }

    @Test
    public void shouldMapperDelegatorFieldReturnFieldSpecWithMapperDelegatorField() throws Exception {
        // given
        // when
        FieldSpec spec = CodeBlockUtils.mapperDelegatorField();
        // then
        Assert.assertEquals("private final pl.openrest.dto.mapper.MapperDelegator mapperDelegator;\n", spec.toString());
    }

    @Test
    public void shouldCollectionVariableReturnStatementWithFallbackedCollectionTypeWhenCollectionTypeIsInterface() throws Exception {
        // given
        // when
        CodeBlock codeBlock = CodeBlockUtils.collectionVariable("list", List.class, Object.class);
        // then
        Assert.assertEquals("java.util.ArrayList<java.lang.Object> list = new java.util.ArrayList<java.lang.Object>();\n",
                codeBlock.toString());
    }

    @Test
    public void shouldCollectionVariableReturnStatementWithNonFallbackCollectionType() throws Exception {
        // given
        // when
        CodeBlock codeBlock = CodeBlockUtils.collectionVariable("list", ArrayList.class, Object.class);
        // then
        Assert.assertEquals("java.util.ArrayList<java.lang.Object> list = new java.util.ArrayList<java.lang.Object>();\n",
                codeBlock.toString());
    }

    @Test
    public void shouldConstructorReturnMethodSpecForPublicConstructorWithMapperDelegatorParameter() throws Exception {
        // given
        // when
        MethodSpec constructor = CodeBlockUtils.constructor();
        // then
        Assert.assertNotNull(constructor);
        Assert.assertEquals(1, constructor.parameters.size());
        Assert.assertEquals("mapperDelegator", constructor.parameters.get(0).name);
        Assert.assertEquals(TypeName.get(MapperDelegator.class), constructor.parameters.get(0).type);
        Assert.assertThat(constructor.modifiers, Matchers.contains(Modifier.PUBLIC));
    }

}
