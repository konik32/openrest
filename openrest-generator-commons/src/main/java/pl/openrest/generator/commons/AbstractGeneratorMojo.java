package pl.openrest.generator.commons;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;
import org.reflections.scanners.Scanner;

import pl.openrest.generator.commons.type.EnumTypeResolver;
import pl.openrest.generator.commons.type.SameTypeResolver;
import pl.openrest.generator.commons.type.TypeFileWriter;
import pl.openrest.generator.commons.type.TypeResolver;
import pl.openrest.generator.commons.type.TypeResolverComposite;

public abstract class AbstractGeneratorMojo extends AbstractMojo {

    @Parameter(required = true, defaultValue = "${project.build.directory}/generated-sources/java")
    protected File outputDirectory;

    @Parameter
    protected RemoteClassNamingStrategy defaultNamingStrategy;

    @Parameter
    protected List<TypeResolver> typeResolvers;

    @Parameter(required = true, readonly = true)
    protected List<String> packagesToScan;

    @Parameter
    protected List<Scanner> scanners;

    @Parameter
    protected ReflectionsFactory reflectionsFactory;

    protected Configuration configuration;

    public void execute() throws MojoExecutionException {
        initializeDefault();
        configuration = createConfiguration();
        configuration.initializeConfigurationAware();
        configuration.afterPropertiesSet();
        doExecute();
    }

    protected abstract void doExecute() throws MojoExecutionException;

    protected Configuration createConfiguration() throws MojoExecutionException {
        Configuration configuration = new Configuration();
        configuration.put("defaultNamingStrategy", defaultNamingStrategy);
        configuration.put("outputDirectory", outputDirectory);
        configuration.put("typeFileWriter", new TypeFileWriter(outputDirectory));

        addDefaultScanners(scanners);
        configuration.put("reflections", reflectionsFactory.create(packagesToScan, scanners));

        addDefaultTypeResolvers(typeResolvers);
        configuration.put("defaultTypeResolver", new TypeResolverComposite(typeResolvers));
        return configuration;
    }

    protected void addDefaultTypeResolvers(List<TypeResolver> typeResolvers) throws MojoExecutionException {
        typeResolvers.add(new EnumTypeResolver());
        typeResolvers.add(new SameTypeResolver());
    }

    protected void initializeDefault() throws MojoExecutionException {
        if (defaultNamingStrategy == null)
            defaultNamingStrategy = new DefaultRemoteClassNamingStrategy();
        if (reflectionsFactory == null)
            reflectionsFactory = new DefaultReflectionsFactory();
        if (typeResolvers == null)
            typeResolvers = new LinkedList<>();
        if (packagesToScan == null)
            packagesToScan = new LinkedList<>();
        if (scanners == null)
            scanners = new LinkedList<>();
    }

    protected abstract void addDefaultScanners(List<Scanner> scanners) throws MojoExecutionException;

    public void setOutputDirectory(File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public void setDefaultNamingStrategy(RemoteClassNamingStrategy defaultNamingStrategy) {
        this.defaultNamingStrategy = defaultNamingStrategy;
    }

    public void setTypeResolvers(List<TypeResolver> typeResolvers) {
        this.typeResolvers = typeResolvers;
    }

    public void setPackagesToScan(List<String> packagesToScan) {
        this.packagesToScan = packagesToScan;
    }

    public void setScanners(List<Scanner> scanners) {
        this.scanners = scanners;
    }

    public void setReflectionsFactory(ReflectionsFactory reflectionsFactory) {
        this.reflectionsFactory = reflectionsFactory;
    }

}
