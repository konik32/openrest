package pl.openrest.dto.mappers.generator.utils;

import java.util.ArrayList;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;

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
    public void shouldCollectionLoopReturnCodeBlockWithForLoopWithTypeAndGetterLiteral() throws Exception {
        // given
        // when
        CodeBlock codeBlock = CodeBlockUtils.collecionLoop(Object.class, "dto.getObjects()", CodeBlock.builder().build());
        // then
        Assert.assertThat(codeBlock.toString(), Matchers.containsString("for(java.lang.Object o: dto.getObjects())"));
    }

    @Test
    public void shouldWrapWithNotNullOrNullableIfReturnCodeBlockWithIfWithGetterLiteralAndNullableGetterLiteral() throws Exception {
        // given
        // when
        CodeBlock codeBlock = CodeBlockUtils.wrapWithNotNullOrNullableIf("dto.getObject()", "dto.isObjectSet()", CodeBlock.builder()
                .build());
        // then
        Assert.assertThat(codeBlock.toString(), Matchers.containsString("if(dto.getObject() != null || dto.isObjectSet())"));
    }

    @Test
    public void shouldWrapWithNotNullIfReturnCodeBlockWithIfWithGetterLiteral() throws Exception {
        // given
        // when
        CodeBlock codeBlock = CodeBlockUtils.wrapWithNotNullIf("dto.getObject()", CodeBlock.builder().build());
        // then
        Assert.assertThat(codeBlock.toString(), Matchers.containsString("if(dto.getObject() != null)"));
    }

    @Test
    public void shouldWrapWithNullableIfReturnCodeBlockWithIfWithNullableGetterLiteral() throws Exception {
        // given
        // when
        CodeBlock codeBlock = CodeBlockUtils.wrapWithNullableIf("dto.isObjectSet()", CodeBlock.builder().build());
        // then
        Assert.assertThat(codeBlock.toString(), Matchers.containsString("if(dto.isObjectSet())"));
    }

    @Test
    public void shouldWrapWithNullableIfReturnIfWithWrappedCodeBlock() throws Exception {
        // given
        // when
        CodeBlock codeBlock = CodeBlockUtils.wrapWithNullableIf("dto.isObjectSet()", CodeBlock.builder().add("codeBlock").build());
        // then
        Assert.assertThat(codeBlock.toString().replaceAll("\\s", ""), Matchers.containsString("{codeBlock}"));
    }

    @Test
    public void shouldWrapWithNotNullOrNullableIfReturnIfWithWrappedCodeBlock() throws Exception {
        // given
        // when
        CodeBlock codeBlock = CodeBlockUtils.wrapWithNotNullOrNullableIf("dto.getObject()", "dto.isObjectSet()",
                CodeBlock.builder().add("codeBlock").build());
        // then
        Assert.assertThat(codeBlock.toString().replaceAll("\\s", ""), Matchers.containsString("{codeBlock}"));
    }

    @Test
    public void shouldWrapWithNotNullIfReturnIfWithWrappedCodeBlock() throws Exception {
        // given
        // when
        CodeBlock codeBlock = CodeBlockUtils.wrapWithNotNullIf("dto.getObjectSet()", CodeBlock.builder().add("codeBlock").build());
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
        Assert.assertThat(spec.toString(), Matchers.containsString("(pl.openrest.dto.mapper.MapperDelegator mapperDelegator)"));
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

}
