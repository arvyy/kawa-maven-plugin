package com.github.arvyy.kawaplugin;

import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.*;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.*;

import java.util.List;

/**
 * Mojo for running main
 */
@Mojo(name = "run", requiresDependencyResolution = ResolutionScope.RUNTIME)
public class RunSchemeMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    MavenProject project;

    @Parameter(property = "run-command", required = false)
    List<String> compileCommand;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        var cmd = compileCommand;
        if (cmd.isEmpty()) {
            cmd = List.of(
                    "java",
                    "-Dkawa.import.path=@KAWAIMPORT@SEPARATOR@PROJECTROOT/src/main/scheme",
                    "kawa.repl",
                    "@PROJECTROOT/src/main/scheme/main.scm");
        }
        MavenKawaInvoker.invokeKawa(cmd, project, getLog());
    }

    /*
    @Override
    protected List<String> getPBCommands() {
        String mainPath = new File(schemeRoot, schemeMain).getAbsolutePath();
        if (schemeMainArgs == null || schemeMainArgs.isEmpty())
            return Arrays.asList(mainPath);
        ArrayList<String> pb = new ArrayList<>();
        pb.add(mainPath);
        pb.addAll(schemeMainArgs);
        System.out.println("PB");
        System.out.println(pb);
        return pb;
    }
     */
}
