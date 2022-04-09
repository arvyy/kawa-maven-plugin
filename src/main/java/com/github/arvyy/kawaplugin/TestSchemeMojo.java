package com.github.arvyy.kawaplugin;

import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.*;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.File;

/**
 * Mojo for invoking unit test
 */
@Mojo(
    name = "test", 
    requiresDependencyResolution = ResolutionScope.TEST,
    defaultPhase = LifecyclePhase.TEST
)
public class TestSchemeMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    MavenProject project;

    @Parameter(property = "test-command", required = false)
    List<String> compileCommand;

    @Parameter(property = "skipTests", defaultValue = "false")
    Boolean skipTests;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (Boolean.TRUE.equals(skipTests)) {
            return;
        }
        var cmd = compileCommand;
        if (cmd.isEmpty()) {
            cmd = List.of(
                    "java",
                    "-Dkawa.import.path=@KAWAIMPORT@SEPARATOR@PROJECTROOT/src/main/scheme@SEPARATOR@PROJECTROOT/src/test/scheme",
                    "kawa.repl",
                    "@PROJECTROOT/target/testrunner.scm");
        }
        try {
            var testrunnerPath = Path.of(project.getBasedir().getAbsolutePath()).resolve("target").resolve("testrunner.scm");
            Files.createDirectories(testrunnerPath.getParent());
            Files.deleteIfExists(testrunnerPath);
            Files.copy(TestSchemeMojo.class.getResource("/testrunner.scm").openStream(), testrunnerPath);
            var code = MavenKawaInvoker.invokeKawa(cmd, project, getLog());
            Files.deleteIfExists(testrunnerPath);
            if (code != 0) {
                throw new MojoFailureException("Test failures");
            }
        } catch (IOException e) {
            throw new MojoExecutionException(e);
        }
    }

}
