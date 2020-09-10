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
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.function.Function;
import java.util.function.BiPredicate;


 
/**
 * Base abstract kawa mojo
 */
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

    /**
     * @return list of commands to be appended to the process builder for making kawa execution
     */
    protected abstract List<String> getPBCommands();

    protected List<String> getClassPathElements(MavenProject project) throws Exception {
        return project.getCompileClasspathElements();
    }

    /**
     * @return list of paths passed to -Dkawa.import.path
     */
    protected List<String> kawaImportPaths() {
        String kawaImportBase = new File(projectDir, schemeRoot).getAbsolutePath();
        String kawaImportLib = new File(projectDir, schemeLibRoot).getAbsolutePath();
        return Arrays.asList(kawaImportBase, kawaImportLib);
    }

    /**
     * action after kawa process ended
     *
     * @param statusCode kawa process exit code
     */
    protected void onProcessEnd(int statusCode) throws MojoExecutionException {
    }

    public void execute() throws MojoExecutionException
    {
        try {
            schemeMainExists = new File(schemeRoot, schemeMain).exists();
            if (schemeMainExists) {
                schemeCompileTargets = Arrays.asList(new File(schemeRoot, schemeMain).getAbsolutePath());
            } else {
                Path schemeRootPath = new File(schemeRoot).toPath();
                schemeCompileTargets = Files.find(Paths.get(schemeRoot), 999, 
                    // for some reason maven crashes when using lambdas...
                    new BiPredicate<>(){
                        @Override
                        public boolean test(Path path, BasicFileAttributes attr) {
                            File f = path.toFile();
                            return !f.isDirectory() && f.getName().endsWith(".scm");
                        }
                    })
                    .map(new Function<Path, String>(){
                        @Override
                        public String apply(Path path) {
                            return path.toString();
                        }
                    })
                    .collect(Collectors.toList());
            }
            projectDir = new File("./");
            String kawaImportPathsString = kawaImportPaths().stream().collect(Collectors.joining(":"));
            String kawaImport = String.format("-Dkawa.import.path=%s", kawaImportPathsString);
            List<String> commands = new ArrayList<>(Arrays.asList("java", kawaImport, "kawa.repl"));
            commands.addAll(getPBCommands());
            var pb = new ProcessBuilder(commands);
            var envVars = pb.environment();
            envVars.put("CLASSPATH", makeCPString(getClassPathElements(project)));
            pb.inheritIO();
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
