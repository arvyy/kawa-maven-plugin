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

import java.nio.Path;
import java.nio.Paths;

 
public abstract class BaseKawaMojo extends AbstractMojo
{

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    protected MavenProject project;

    @Parameter(property = "schemeRoot", defaultValue = "./src/main/scheme/", required = false)
    protected String schemeRoot;

    @Parameter(property = "schemeTestRoot", defaultValue = "./src/test/scheme/", required = false)
    protected String schemeTestRoot;

    @Parameter(property = "schemeLibRoot", defaultValue = "./lib/", required = false)
    protected String schemeLibRoot;

    @Parameter(property = "schemeMain", defaultValue = "main.scm")
    protected String schemeMain;

    protected boolean schemeMainExists;

    protected List<String> schemeCompileTargets;

    @Parameter(property = "schemeTestMain", defaultValue = "main-test.scm")
    protected String schemeTestMain;

    protected File projectDir;

    protected abstract List<String> getPBCommands();

    protected File executeDir() {
        return new File(schemeRoot);
    }

    protected List<String> kawaImportPaths() {
        String kawaImportBase = new File(projectDir, schemeRoot).getAbsolutePath();
        String kawaImportLib = new File(projectDir, schemeLibRoot).getAbsolutePath();
        return Arrays.asList(kawaImportBase, kawaImportLib);
    }

    protected void onProcessEnd(int statusCode) throws MojoExecutionException {
    }

    public void execute() throws MojoExecutionException
    {
        try {
            schemeMainExists = new File(schemeRoot, schemeMain).exists();
            if (schemeMainExists) {
                schemeCompileTargets = Arrays.asList(schemeMain);
            } else {
                Path schemeRootPath = new File(schemeRoot).getPath();
                schemeCompileTargets = Files.find(Paths.get(schemeRoot), 999, 
                        (path, attr) -> {
                            File f = path.toFile();
                            return !f.isDirectory() && f.getName().endsWith(".scm");
                        })
                    .map(path -> path.relativize(schemeRootPath))
                    .map(path -> path.toString())
                    .collect(Collectors.toList());
            }
            projectDir = new File("./");
            String kawaImportPathsString = kawaImportPaths().stream().collect(Collectors.joining(":"));
            String kawaImport = String.format("-Dkawa.import.path=%s", kawaImportPathsString);
            List<String> commands = new ArrayList<>(Arrays.asList("java", kawaImport, "kawa.repl"));
            commands.addAll(getPBCommands());
            var pb = new ProcessBuilder(commands);
            var envVars = pb.environment();
            envVars.put("CLASSPATH", makeCPString(project.getCompileClasspathElements()));
            pb.inheritIO();
            pb.directory(executeDir());
            int code = pb.start().waitFor();
            onProcessEnd(code);
        } catch (Exception e) {
            throw new MojoExecutionException("Scheme MOJO failed", e);
        }
    }

    private String makeCPString(List<String> locations) {
        var sep = System.getProperty("path.separator");
        return locations.stream()
            .collect(Collectors.joining(sep));
    }
}
