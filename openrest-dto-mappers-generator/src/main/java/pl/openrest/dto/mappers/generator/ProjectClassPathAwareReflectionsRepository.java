package pl.openrest.dto.mappers.generator;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import lombok.NonNull;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.reflections.Reflections;
import org.reflections.scanners.Scanner;
import org.reflections.util.ConfigurationBuilder;

import pl.openrest.generator.commons.ReflectionsFactory;

public class ProjectClassPathAwareReflectionsRepository implements ReflectionsFactory {

    private final List<URL> projectClassPathURLs;
    private final URLClassLoader projectClassLoader;

    public ProjectClassPathAwareReflectionsRepository(@NonNull MavenProject mavenProject) throws MojoExecutionException {
        try {
            this.projectClassPathURLs = getProjectClassPathURLs(mavenProject.getRuntimeClasspathElements());
            this.projectClassLoader = new URLClassLoader(projectClassPathURLs.toArray(new URL[0]), this.getClass().getClassLoader());
        } catch (DependencyResolutionRequiredException e) {
            throw new MojoExecutionException("Cannot get RuntimeClasspathElements", e);
        }
    }

    @Override
    public Reflections create(List<String> packages, List<Scanner> scanners) {
        Reflections reflections = new Reflections(new ConfigurationBuilder().forPackages(packages.toArray(new String[packages.size()]))
                .addScanners(scanners.toArray(new Scanner[scanners.size()])).addUrls(projectClassPathURLs)
                .addClassLoader(projectClassLoader));
        return reflections;
    }

    private List<URL> getProjectClassPathURLs(List<String> classPathElements) throws MojoExecutionException {
        List<URL> projectClasspathList = new ArrayList<URL>();
        for (String element : classPathElements) {
            try {
                projectClasspathList.add(new File(element).toURI().toURL());
            } catch (MalformedURLException e) {
                throw new MojoExecutionException(String.format("Error occured parsing string %s to URL", element), e);
            }
        }
        return projectClasspathList;
    }
}
