package pl.openrest.generator.commons.type;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.squareup.javapoet.TypeSpec;

public class TypeFileWriterTest {

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    private File outputDirectory;

    private TypeFileWriter writer;

    @Before
    public void setUp() throws IOException {
        outputDirectory = testFolder.newFolder();
        writer = new TypeFileWriter(outputDirectory);
    }

    @Test
    public void shouldGenerateRemotePredicateRepositoryJavaFile() throws Exception {
        // given
        TypeSpec typeSpec = TypeSpec.classBuilder("TestClass").build();
        // when
        writer.write(typeSpec, "test");
        // then
        Assert.assertEquals(1, outputDirectory.listFiles().length);
        Assert.assertEquals(1, outputDirectory.listFiles()[0].listFiles().length);
        Assert.assertEquals("TestClass.java", outputDirectory.listFiles()[0].listFiles()[0].getName());
    }
}
