package com.github.arvyy.kawaplugin;

import org.apache.maven.project.*;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.plugin.MojoExecutionException;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.File;

 
@Mojo(name = "test", requiresDependencyResolution = ResolutionScope.TEST)
public class TestSchemeMojo extends BaseKawaMojo {

    @Override
    protected List<String> getPBCommands() {
        return Arrays.asList(schemeTestMain);
    }

    @Override
    protected File executeDir() {
        return new File(schemeTestRoot);
    }

    @Override
    protected void onProcessEnd(int code) throws MojoExecutionException {
        if (code != 0) {
            throw new MojoExecutionException("Test failures");
        }
    }

    @Override
    protected List<String> kawaImportPaths() {
        ArrayList<String> lst = new ArrayList<>(super.kawaImportPaths());
        lst.add(new File(projectDir, schemeTestRoot).getAbsolutePath());
        return lst;
    }

}
