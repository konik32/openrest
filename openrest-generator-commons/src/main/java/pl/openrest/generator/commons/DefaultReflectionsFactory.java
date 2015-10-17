package pl.openrest.generator.commons;

import java.util.List;

import org.reflections.Reflections;
import org.reflections.scanners.Scanner;
import org.reflections.util.ConfigurationBuilder;


public class DefaultReflectionsFactory implements ReflectionsFactory {

    @Override
    public Reflections create(List<String> packages, List<Scanner> scanners) {
        Reflections reflections = new Reflections(new ConfigurationBuilder().forPackages(packages.toArray(new String[packages.size()]))
                .addScanners(scanners.toArray(new Scanner[scanners.size()])));
        return reflections;
    }
}
