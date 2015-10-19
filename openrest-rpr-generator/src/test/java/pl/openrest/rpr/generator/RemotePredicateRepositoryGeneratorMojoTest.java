package pl.openrest.rpr.generator;

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

public class RemotePredicateRepositoryGeneratorMojoTest {

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    private File outputDirectory;

    private RemotePredicateRepositoryGeneratorMojo mojo;

    @Before
    public void setUp() throws IOException {
        mojo = new RemotePredicateRepositoryGeneratorMojo();
        outputDirectory = testFolder.newFolder();
        mojo.setOutputDirectory(outputDirectory);
        mojo.setPackagesToScan(Arrays.asList("pl.openrest.rpr.generator.test.repository"));
    }

    @Test
    public void shouldGenerateRemoteUserPredicateRepository() throws Exception {
        // given
        // when
        mojo.execute();
        // then
        File generatedDto = new File(outputDirectory.getAbsolutePath()
                + "/pl/openrest/rpr/generator/test/repository/RUserPredicateRepository.java");
        byte[] encoded = Files.readAllBytes(generatedDto.toPath());
        String actual = new String(encoded, "UTF-8").replaceAll("\\s", "");
        String expected = Resources.toString(Resources.getResource("RUserPredicateRepository.txt"), Charsets.UTF_8).replaceAll("\\s", "");
        Assert.assertEquals(expected, actual);
    }
}
