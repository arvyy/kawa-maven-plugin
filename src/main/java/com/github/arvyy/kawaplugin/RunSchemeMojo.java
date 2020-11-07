package com.github.arvyy.kawaplugin;

import org.apache.maven.project.*;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.File;

/**
 * Mojo for running main
 */
@Mojo(name = "run", requiresDependencyResolution = ResolutionScope.RUNTIME)
public class RunSchemeMojo extends BaseKawaMojo {
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
}
