package com.github.arvyy.kawaplugin;

import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.*;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Mojo for invoking REPL with project's dependencies
 */
@Mojo(name = "repl", requiresDependencyResolution = ResolutionScope.RUNTIME)
public class ReplKawaMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    MavenProject project;

    @Parameter(property = "replCommand", required = false)
    List<String> replCommand;

    @Parameter(property = "replPort", required = false)
    String replPort;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        var cmd = replCommand;
        if (cmd.isEmpty()) {
            cmd = new ArrayList(List.of(
                    "java",
                    "-Dkawa.import.path=@KAWAIMPORT@SEPARATOR@PROJECTROOT/src/main/scheme",
                    "kawa.repl"));
        }
        var port = replPort;
        if (port != null && !port.isEmpty()) {
            cmd.add("--server");
            cmd.add(port);
        }
        MavenKawaInvoker.invokeKawa(cmd, project, getLog());
    }
}
