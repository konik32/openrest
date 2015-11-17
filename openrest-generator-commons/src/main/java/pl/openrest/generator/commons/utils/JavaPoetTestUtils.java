package pl.openrest.generator.commons.utils;

import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import pl.openrest.generator.commons.type.TypeFileWriter;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

public final class JavaPoetTestUtils {

    private JavaPoetTestUtils() {
        // TODO Auto-generated constructor stub
    }

    public static MethodSpec getMethodSpec(TypeSpec spec, String methodName) {
        for (MethodSpec ms : spec.methodSpecs) {
            if (ms.name.equals(methodName)) {
                return ms;
            }
        }
        return null;
    }

    public static TypeSpec getTypeSpec(TypeFileWriter typeFileWriter) {
        ArgumentCaptor<TypeSpec> dtoSpecCaptor = ArgumentCaptor.forClass(TypeSpec.class);
        Mockito.verify(typeFileWriter).write(dtoSpecCaptor.capture(), Mockito.anyString());
        return dtoSpecCaptor.getValue();
    }

    public static FieldSpec getFieldSpec(TypeSpec spec, String fieldName) {
        for (FieldSpec fs : spec.fieldSpecs) {
            if (fs.name.equals(fieldName)) {
                return fs;
            }
        }
        return null;
    }

    public static CodeBlock getSingleStatementCodeBlock(String statement) {
        return CodeBlock.builder().addStatement(statement).build();
    }

    public static CodeBlock geCodeBlock(String line) {
        return CodeBlock.builder().add(line).build();
    }
}
