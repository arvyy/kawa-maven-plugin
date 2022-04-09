package com.github.arvyy.kawaplugin;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class MavenKawaInvoker {

    private final static String kawaImportPath = "target/classes/kawaLibraries";

    public static int invokeKawa(List<String> commandTemplate, MavenProject mavenProject, Log log) throws MojoExecutionException {
        String classpath = null;
        try {
            classpath = mavenProject.getCompileClasspathElements().stream()
                    .collect(Collectors.joining(File.pathSeparator));
        } catch (DependencyResolutionRequiredException e) {
            throw new MojoExecutionException("Failed to resolve classpath env variable value", e);
        }
        var path  = mavenProject.getBasedir().toPath().resolve(kawaImportPath);
        if (!Files.exists(path)) {
            extractKawaLibraries(mavenProject, path);
        }
        var expandedCommandTemplate = commandTemplate.stream()
                .map(part -> part.replace("@KAWAIMPORT",  path.toAbsolutePath().toString())
                                .replace("@PROJECTROOT", mavenProject.getBasedir().getAbsolutePath())
                        .replace("@SEPARATOR", File.pathSeparator)
                )
                .collect(Collectors.toList());
        log.debug("Using classpath " + classpath);
        log.debug("Executing process " + expandedCommandTemplate);
        var pb = new ProcessBuilder(expandedCommandTemplate);
        var envVars = pb.environment();
        envVars.put("CLASSPATH", classpath);
        pb.directory(mavenProject.getBasedir());
        pb.inheritIO();
        try {
            return pb.start().waitFor();
        } catch (Exception e) {
            throw new MojoExecutionException("Failed to execute command through kawa", e);
        }
    }

    private static void extractKawaLibraries(MavenProject mavenProject, Path path) throws MojoExecutionException {
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to create folder for kawa libraries' sources in " + path, e);
        }
        for (var a: mavenProject.getArtifacts()) {
            if (!a.getType().equals("kawalib"))
                continue;
            try (var is = new ZipInputStream(new BufferedInputStream(Files.newInputStream(a.getFile().toPath())))) {
                ZipEntry e;
                while ((e = is.getNextEntry()) != null) {
                    if (e.isDirectory())
                        continue;
                    var outputPath = path.resolve(e.getName());
                    Files.createDirectories(outputPath.getParent());
                    Files.copy(is, path.resolve(e.getName()));
                }
            } catch (IOException e) {
                throw new MojoExecutionException("Failed to extract kawalib content for artifact " + a, e);
            }
        }
    }

}
