package com.github.arvyy.kawaplugin;

import org.apache.maven.project.*;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.*;

import java.util.List;
import java.util.Arrays;
import java.io.File;

 
@Mojo(name = "run", requiresDependencyResolution = ResolutionScope.RUNTIME)
public class RunSchemeMojo extends BaseKawaMojo {
    @Override
    protected List<String> getPBCommands() {
        return Arrays.asList(schemeMain);
    }
}
