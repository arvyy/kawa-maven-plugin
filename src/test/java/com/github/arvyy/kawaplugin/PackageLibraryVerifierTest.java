package com.github.arvyy.kawaplugin;

import junit.framework.TestCase;
import org.apache.maven.it.VerificationException;
import org.apache.maven.it.Verifier;
import org.apache.maven.it.util.ResourceExtractor;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

public class PackageLibraryVerifierTest extends TestCase {
    public void testMyPlugin() throws Exception {
        File testLibDir = ResourceExtractor.simpleExtractResources(getClass(), "/testLib");
        File testAppDir = ResourceExtractor.simpleExtractResources(getClass(), "/testApp");

        Verifier verifier;

        /*
            Run test on test-lib project
         */
        verifier = new Verifier(testLibDir.getAbsolutePath());
        verifier.deleteArtifact("com.github.arvyy.test", "test-lib", "0.0.1", "pom");
        verifier.deleteArtifact("com.github.arvyy.test", "test-lib", "0.0.1", "zip");
        verifier.executeGoal("kawa:test");
        verifier.verifyErrorFreeLog();
        verifier.resetStreams();


        /*
            Install test-lib project
         */
        verifier = new Verifier(testLibDir.getAbsolutePath());
        verifier.deleteArtifact("com.github.arvyy.test", "test-lib", "0.0.1", "pom");
        verifier.deleteArtifact("com.github.arvyy.test", "test-lib", "0.0.1", "zip");

        verifier.executeGoal("install");
        verifier.verifyArtifactPresent("com.github.arvyy.test", "test-lib", "0.0.1", "pom");
        verifier.verifyArtifactPresent("com.github.arvyy.test", "test-lib", "0.0.1", "zip");
        verifier.verifyErrorFreeLog();
        verifier.resetStreams();

        /*
            compile test-app project including depencency on test-lib
         */
        verifier = new Verifier(testAppDir.getAbsolutePath());
        verifier.deleteArtifact("com.github.arvyy.test", "test-app", "0.0.1", "pom");
        verifier.deleteArtifact("com.github.arvyy.test", "test-app", "0.0.1", "jar");

        verifier.executeGoals(List.of("kawa:compile", "install"));
        verifier.verifyArtifactPresent("com.github.arvyy.test", "test-app", "0.0.1", "pom");
        verifier.verifyArtifactPresent("com.github.arvyy.test", "test-app", "0.0.1", "jar");
        verifier.verifyErrorFreeLog();
        verifier.resetStreams();

        /*
            run test-app project, check if it scheme implementation creates file test.out with content test1\ntest2
         */
        verifier = new Verifier(testAppDir.getAbsolutePath());
        verifier.deleteArtifact("com.github.arvyy.test", "test-app", "0.0.1", "pom");
        verifier.deleteArtifact("com.github.arvyy.test", "test-app", "0.0.1", "jar");
        Files.deleteIfExists(testAppDir.toPath().resolve("test.out"));

        verifier.executeGoals(List.of("kawa:run"));
        assertEquals(Files.readAllLines(testAppDir.toPath().resolve("test.out")), List.of("ok1", "ok2"));
        verifier.verifyErrorFreeLog();
        verifier.resetStreams();

        /*
            run tests in test-app project (which intentionally fails), check handling
         */
        verifier = new Verifier(testAppDir.getAbsolutePath());
        try {
            verifier.executeGoals(List.of("kawa:test"));
            verifier.verifyErrorFreeLog();
            fail();
        } catch (VerificationException ignored) {
        }
        verifier.resetStreams();

        /*
            run test with -DskipTests=true, check it doesn't run & fail
         */
        verifier = new Verifier(testAppDir.getAbsolutePath());
        verifier.setSystemProperty("skipTests", "true");
        verifier.executeGoals(List.of("kawa:test"));
        verifier.verifyErrorFreeLog();
        verifier.resetStreams();

    }
}