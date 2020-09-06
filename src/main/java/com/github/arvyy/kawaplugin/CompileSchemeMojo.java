package com.github.arvyy.kawaplugin;

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
public class CompileSchemeMojo extends BaseKawaMojo {
    @Override
    protected List<String> getPBCommands() {
        String target = new File(projectDir, "target/classes").getAbsolutePath();
        String compileTargetsString = schemeCompileTargets
            .stream()
            .collect(Collectors.joining(" "));
        return Arrays.asList("-d", target, "-C", compileTargetsString);
    }
}
