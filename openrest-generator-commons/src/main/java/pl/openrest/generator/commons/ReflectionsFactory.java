package pl.openrest.generator.commons;

import java.util.List;

import org.reflections.Reflections;
import org.reflections.scanners.Scanner;

public interface ReflectionsFactory {

    Reflections create(List<String> packages, List<Scanner> scanners);
}
