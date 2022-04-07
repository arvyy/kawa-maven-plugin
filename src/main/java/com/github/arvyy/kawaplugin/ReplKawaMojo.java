package com.github.arvyy.kawaplugin;

import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.*;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.*;

import java.util.List;

/**
 * Mojo for invoking REPL with project's dependencies
 */
@Mojo(name = "repl", requiresDependencyResolution = ResolutionScope.RUNTIME)
public class ReplKawaMojo extends AbstractMojo {
    /*
    @Override
    protected List<String> getPBCommands() {
        return Arrays.asList();
    }
     */

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    MavenProject project;

    @Parameter(property = "repl-command", required = false)
    List<String> replCommand;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        var cmd = replCommand;
        if (cmd.isEmpty()) {
            cmd = List.of(
                    "java",
                    "-Dkawa.import.path=@KAWAIMPORT@SEPARATOR@PROJECTROOT/src/main/scheme",
                    "kawa.repl");
        }
        MavenKawaInvoker.invokeKawa(cmd, project, getLog());
    }
}
