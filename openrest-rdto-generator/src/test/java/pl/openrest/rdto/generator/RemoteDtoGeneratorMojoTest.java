package pl.openrest.rdto.generator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class RemoteDtoGeneratorMojoTest {

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    private File outputDirectory;

    private RemoteDtoGeneratorMojo mojo;

    @Before
    public void setUp() throws IOException {
        mojo = new RemoteDtoGeneratorMojo();
        outputDirectory = testFolder.newFolder();
        mojo.setOutputDirectory(outputDirectory);
        mojo.setPackagesToScan(Arrays.asList("pl.openrest.rdto.generator.test.dto"));
    }

    @Test
    public void shouldGenerateRemoteUserDto() throws Exception {
        // given
        // when
        mojo.execute();
        // then
        File generatedDto = new File(outputDirectory.getAbsolutePath() + "/pl/openrest/rdto/generator/test/dto/RUserDto.java");
        byte[] encoded = Files.readAllBytes(generatedDto.toPath());
        String actual = new String(encoded, "UTF-8").replaceAll("\\s", "");
        String expected = Resources.toString(Resources.getResource("RUserDto.txt"), Charsets.UTF_8).replace("\r\n", "\n")
                .replaceAll("\\s", "");
        Assert.assertEquals(expected, actual);
    }
}
