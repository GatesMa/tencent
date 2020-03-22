/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Other licenses:
 * -----------------------------------------------------------------------------
 * Commercial licenses for this work are available. These replace the above
 * ASL 2.0 and offer limited warranties, support, maintenance, and commercial
 * database integrations.
 *
 * For more information, please visit: http://www.jooq.org/licenses
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */
package org.jooq.codegen.maven;

import static org.apache.maven.plugins.annotations.LifecyclePhase.GENERATE_SOURCES;
import static org.apache.maven.plugins.annotations.ResolutionScope.TEST;
import static org.jooq.Constants.XSD_CODEGEN;
import static org.jooq.codegen.GenerationTool.DEFAULT_TARGET_DIRECTORY;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import org.jooq.codegen.GenerationTool;
import org.jooq.meta.jaxb.Configuration;
import org.jooq.meta.jaxb.Target;
import org.jooq.util.jaxb.tools.MiniJAXB;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

/**
 * The jOOQ Codegen Plugin
 *
 * @author Sander Plas
 * @author Lukas Eder
 */
@Mojo(
    name = "generate",
    defaultPhase = GENERATE_SOURCES,
    requiresDependencyResolution = TEST,
    threadSafe = true
)
public class Plugin extends AbstractMojo {

    /**
     * The Maven project.
     */
    @Parameter(
        property = "project",
        required = true,
        readonly = true
    )
    private MavenProject                 project;

    /**
     * An external configuration file that is appended to anything from the
     * Maven configuration, using Maven's <code>combine.children="append"</code>
     * semantics.
     */
    @Parameter(
        property = "jooq.codegen.configurationFile"
    )
    private String                       configurationFile;

    /**
     * An external set of configuration files that is appended to anything from
     * the Maven configuration, using Maven's
     * <code>combine.children="append"</code> semantics.
     */
    @Parameter(
        property = "jooq.codegen.configurationFiles"
    )
    private List<String>                 configurationFiles;

    /**
     * The base directory that should be used instead of the JVM's working
     * directory, to resolve all relative paths.
     */
    @Parameter(
        property = "jooq.codegen.basedir"
    )
    private String                       basedir;

    /**
     * Whether to skip the execution of the Maven Plugin for this module.
     */
    @Parameter(
        property = "jooq.codegen.skip"
    )
    private boolean                      skip;

    /**
     * The logging threshold.
     */
    @Parameter(
        property = "jooq.codegen.logging"
    )
    private org.jooq.meta.jaxb.Logging   logging;

    /**
     * The on-error behavior.
     */
    @Parameter(
        property = "jooq.codegen.onError"
    )
    private org.jooq.meta.jaxb.OnError   onError;

    /**
     * The jdbc settings.
     */
    @Parameter
    private org.jooq.meta.jaxb.Jdbc      jdbc;

    /**
     * The generator settings
     */
    @Parameter
    private org.jooq.meta.jaxb.Generator generator;

    @Override
    public void execute() throws MojoExecutionException {
        if (skip) {
            getLog().info("Skipping jOOQ code generation");
            return;
        }

        if (configurationFiles != null && !configurationFiles.isEmpty())
            for (String file : configurationFiles)
                read(file);
        else if (configurationFile != null)
            read(configurationFile);

        // [#5286] There are a variety of reasons why the generator isn't set up
        //         correctly at this point. We'll log them all here.
        if (generator == null) {
            getLog().error("Incorrect configuration of jOOQ code generation tool");
            getLog().error(
                  "\n"
                + "The jOOQ-codegen-maven module's generator configuration is not set up correctly.\n"
                + "This can have a variety of reasons, among which:\n"
                + "- Your pom.xml's <configuration> contains invalid XML according to " + XSD_CODEGEN + "\n"
                + "- There is a version or artifact mismatch between your pom.xml and your commandline");

            throw new MojoExecutionException("Incorrect configuration of jOOQ code generation tool. See error above for details.");
        }

        ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
        URLClassLoader pluginClassLoader = getClassLoader();

        try {

            // [#2886] Add the surrounding project's dependencies to the current classloader
            Thread.currentThread().setContextClassLoader(pluginClassLoader);

            // [#9727] The Maven basedir may be overridden by explicit configuration
            String actualBasedir = basedir == null ? project.getBasedir().getAbsolutePath() : basedir;

            // [#5881] Target is allowed to be null
            if (generator.getTarget() == null)
                generator.setTarget(new Target());

            if (generator.getTarget().getDirectory() == null)
                generator.getTarget().setDirectory(DEFAULT_TARGET_DIRECTORY);

            Configuration configuration = new Configuration();
            configuration.setLogging(logging);
            configuration.setOnError(onError);
            configuration.setJdbc(jdbc);
            configuration.setGenerator(generator);
            configuration.setBasedir(actualBasedir);

            if (getLog().isDebugEnabled())
                getLog().debug("Using this configuration:\n" + configuration);

            GenerationTool.generate(configuration);
        }
        catch (Exception ex) {
            throw new MojoExecutionException("Error running jOOQ code generation tool", ex);
        }
        finally {

            // [#2886] Restore old class loader
            Thread.currentThread().setContextClassLoader(oldCL);


            // [#7630] Close URLClassLoader to help free resources
            try {
                pluginClassLoader.close();
            }

            // Catch all possible errors to avoid suppressing the original exception
            catch (Throwable e) {
                getLog().error("Couldn't close the classloader.", e);
            }

        }

        project.addCompileSourceRoot(generator.getTarget().getDirectory());
    }

    private void read(String file) {
        getLog().info("Reading external configuration: " + file);
        File f = new File(file);

        if (!f.isAbsolute())
            f = new File(project.getBasedir(), file);

        FileInputStream in = null;
        try {
            in = new FileInputStream(f);
            Configuration configuration = GenerationTool.load(in);
            logging = MiniJAXB.append(logging, configuration.getLogging());
            onError = MiniJAXB.append(onError, configuration.getOnError());
            jdbc = MiniJAXB.append(jdbc, configuration.getJdbc());
            generator = MiniJAXB.append(generator, configuration.getGenerator());
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                }
                catch (IOException ignore) {}
            }
        }
    }

    private URLClassLoader getClassLoader() throws MojoExecutionException {
        try {
            List<String> classpathElements = project.getRuntimeClasspathElements();
            URL urls[] = new URL[classpathElements.size()];

            for (int i = 0; i < urls.length; i++)
                urls[i] = new File(classpathElements.get(i)).toURI().toURL();

            return new URLClassLoader(urls, getClass().getClassLoader());
        }
        catch (Exception e) {
            throw new MojoExecutionException("Couldn't create a classloader.", e);
        }
    }
}
