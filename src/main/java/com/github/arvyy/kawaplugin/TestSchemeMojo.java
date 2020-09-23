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

/**
 * Mojo for invoking unit test
 */
@Mojo(
    name = "test", 
    defaultPhase = LifecyclePhase.TEST,
    requiresDependencyResolution = ResolutionScope.TEST
)
public class TestSchemeMojo extends BaseKawaMojo {

    @Override
    protected List<String> getPBCommands() {
        return Arrays.asList(new File(schemeTestRoot, schemeTestMain).getAbsolutePath());
    }

    @Override
    protected List<String> getClassPathElements(MavenProject project) throws Exception {
        return project.getTestClasspathElements();
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

    @Override
    public void execute() throws MojoExecutionException {
        if (!new File(schemeTestRoot, schemeTestMain).exists())
            return;
        super.execute();
    }

}
