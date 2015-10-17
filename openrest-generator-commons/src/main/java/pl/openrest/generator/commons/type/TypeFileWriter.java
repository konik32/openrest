package pl.openrest.generator.commons.type;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

public class TypeFileWriter {

    private final File outputDirectory;

    private static final Logger LOGGER = LoggerFactory.getLogger(TypeFileWriter.class);

    public TypeFileWriter(File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public void write(TypeSpec typeSpec, String packageName) {
        JavaFile file = JavaFile.builder(packageName, typeSpec).build();
        try {
            file.writeTo(outputDirectory);
        } catch (IOException e) {
            LOGGER.error(String.format("Error while writing %s", typeSpec.name), e);
        }
    }
}
