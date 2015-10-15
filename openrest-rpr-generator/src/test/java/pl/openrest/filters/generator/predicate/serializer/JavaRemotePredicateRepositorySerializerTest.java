package pl.openrest.filters.generator.predicate.serializer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import pl.openrest.filters.generator.predicate.context.PredicateInformationFactory.ParameterInformation;
import pl.openrest.filters.generator.predicate.context.PredicateRepositoryInformation;
import pl.openrest.filters.remote.predicate.FilterPredicate;
import pl.openrest.filters.remote.predicate.SearchPredicate;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class JavaRemotePredicateRepositorySerializerTest {

    private PredicateRepositoryInformation repoInfo;

    private RemotePredicateRepositoryNamingStrategy namingStrategy = new DefaultRemotePredicateRepositoryNamingStrategy();

    private JavaRemotePredicateRepositorySerializer serializer;
    private File outputDirectory;

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @Before
    public void setUp() throws IOException {
        repoInfo = new PredicateRepositoryInformation(TestPredicateRepository.class, Object.class,
                "pl.openrest.filters.generator.predicate.serializer", false);
        repoInfo.addPredicate(new SearchPredicate("nameEq", true, new ParameterInformation("name", String.class)));
        repoInfo.addPredicate(new FilterPredicate("active"));
        outputDirectory = testFolder.newFolder();
        serializer = new JavaRemotePredicateRepositorySerializer(outputDirectory, namingStrategy);
    }

    @Test
    public void shouldGenerateRemotePredicateRepositoryJavaFile() throws Exception {
        // given
        // when
        serializer.serialize(Arrays.asList(repoInfo));
        // then
        File generatedRepository = new File(outputDirectory.getAbsolutePath()
                + "/pl/openrest/filters/generator/predicate/serializer/RTestPredicateRepository.java");
        byte[] encoded = Files.readAllBytes(generatedRepository.toPath());
        String actual = new String(encoded, "UTF-8").replaceAll("\\s", "");
        String expected = Resources
                .toString(Resources.getResource("pl/openrest/filters/generator/predicate/serializer/RTestPredicateRepository.java"),
                        Charsets.UTF_8).replace("\r\n", "\n").replaceAll("\\s", "");
        Assert.assertEquals(expected, actual);

    }

    public class TestPredicateRepository {
    }
}
