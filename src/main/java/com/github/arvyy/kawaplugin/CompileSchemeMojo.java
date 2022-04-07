package com.github.arvyy.kawaplugin;

import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.*;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.*;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.Arrays;
import java.io.File;

/**
 * Mojo for compiling scheme files to .class
 */
@Mojo(
    name = "compile", 
    requiresDependencyResolution = ResolutionScope.COMPILE, 
    defaultPhase = LifecyclePhase.COMPILE
)
public class CompileSchemeMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    MavenProject project;

    @Parameter(property = "compile-command", required = false)
    List<String> compileCommand;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        var cmd = compileCommand;
        if (cmd.isEmpty()) {
            cmd = List.of(
                    "java",
                    "-Dkawa.import.path=@KAWAIMPORT@SEPARATOR@PROJECTROOT/src/main/scheme",
                    "kawa.repl",
                    "-d",
                    "@PROJECTROOT/target/classes",
                    "--main",
                    "-C",
                    "@PROJECTROOT/src/main/scheme/main.scm");
        }
        MavenKawaInvoker.invokeKawa(cmd, project, getLog());
    }

    /*
    @Override
    protected List<String> getPBCommands() {
        String target = new File(projectDir, "target/classes").getAbsolutePath();
        ArrayList<String> lst = new ArrayList<>();
        lst.addAll(Arrays.asList("-d", target));
        if (asMain) {
            lst.add("--main");
        }
        lst.add("-C");
        lst.addAll(schemeCompileTargets);
        return lst;
    }
     */
}
