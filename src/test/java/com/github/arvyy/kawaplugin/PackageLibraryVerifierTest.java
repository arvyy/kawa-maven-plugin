package com.github.arvyy.kawaplugin;

import junit.framework.TestCase;
import org.apache.commons.net.telnet.TelnetClient;
import org.apache.maven.it.VerificationException;
import org.apache.maven.it.Verifier;
import org.apache.maven.it.util.ResourceExtractor;

import java.io.*;
import java.nio.file.Files;
import java.util.List;

public class PackageLibraryVerifierTest extends TestCase {

    final File testLibDir;
    final File testAppDir;

    public PackageLibraryVerifierTest() throws IOException {
        testLibDir = ResourceExtractor.simpleExtractResources(getClass(), "/testLib");
        testAppDir = ResourceExtractor.simpleExtractResources(getClass(), "/testApp");
    }

    Verifier newVerifier(File dir) throws VerificationException {
        var verifier = new Verifier(dir.getAbsolutePath());
        verifier.setSystemProperty("kawa-maven-plugin.version", System.getProperty("kawa-maven-plugin.version"));
        return verifier;
    }

    /*
        Run test on test-lib project
     */
    void goalTestLib() throws Exception {
        var verifier = newVerifier(testLibDir);
        verifier.deleteArtifact("com.github.arvyy.test", "test-lib", "0.0.1", "pom");
        verifier.deleteArtifact("com.github.arvyy.test", "test-lib", "0.0.1", "zip");
        verifier.executeGoal("kawa:test");
        verifier.verifyErrorFreeLog();
        verifier.resetStreams();
    }

    /*
        Run repl on test-lib project, verify it launches a server over the port 12345
     */
    void goalReplServerLib() throws Exception {
        var replThread = new Thread(() -> {
            try {
                var v = newVerifier(testLibDir);
                v.setSystemProperty("replPort", "12345");
                v.executeGoal("kawa:repl");
                v.verifyErrorFreeLog();
                v.resetStreams();
            } catch (VerificationException e) {
                e.printStackTrace();
            }
        });
        replThread.start();
        try {
            Thread.sleep(10000);
        } catch(Exception ignored) {
        }
        var client = new TelnetClient();
        client.connect("localhost", 12345);
        var writer = new OutputStreamWriter(client.getOutputStream());
        writer.write("(+ 1 2)\r\n");
        writer.flush();
        var reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
        var response = reader.readLine();
        writer.write("(exit)\r\n");
        writer.flush();
        client.disconnect();
        replThread.join();
        assertEquals("#|kawa:1|# 3", response);
    }

    /*
        Install test-lib project
     */
    void goalInstallLib() throws Exception {
        var verifier = newVerifier(testLibDir);
        verifier.deleteArtifact("com.github.arvyy.test", "test-lib", "0.0.1", "pom");
        verifier.deleteArtifact("com.github.arvyy.test", "test-lib", "0.0.1", "zip");

        verifier.executeGoal("install");
        verifier.verifyArtifactPresent("com.github.arvyy.test", "test-lib", "0.0.1", "pom");
        verifier.verifyArtifactPresent("com.github.arvyy.test", "test-lib", "0.0.1", "zip");
        verifier.verifyErrorFreeLog();
        verifier.resetStreams();
    }

    /*
        compile test-app project including dependency on test-lib
     */
    void goalCompileApp() throws Exception {
        var verifier = newVerifier(testAppDir);
        verifier.deleteArtifact("com.github.arvyy.test", "test-app", "0.0.1", "pom");
        verifier.deleteArtifact("com.github.arvyy.test", "test-app", "0.0.1", "jar");

        verifier.setSystemProperty("skipTests", "true"); // application tests intentionally broken
        verifier.executeGoals(List.of("kawa:compile", "install"));
        verifier.verifyArtifactPresent("com.github.arvyy.test", "test-app", "0.0.1", "pom");
        verifier.verifyArtifactPresent("com.github.arvyy.test", "test-app", "0.0.1", "jar");
        verifier.verifyErrorFreeLog();
        verifier.resetStreams();
    }

    /*
        run test-app project, check if it scheme implementation creates file test.out with content test1\ntest2\ncmdarg
     */
    void goalRunApp() throws Exception {
        var verifier = newVerifier(testAppDir);
        verifier.deleteArtifact("com.github.arvyy.test", "test-app", "0.0.1", "pom");
        verifier.deleteArtifact("com.github.arvyy.test", "test-app", "0.0.1", "jar");
        Files.deleteIfExists(testAppDir.toPath().resolve("test.out"));

        verifier.setSystemProperty("mainArgs", "cmdarg1, cmdarg2");
        verifier.executeGoals(List.of("kawa:run"));
        assertEquals(Files.readAllLines(testAppDir.toPath().resolve("test.out")), List.of("ok1", "ok2", "cmdarg1"));
        verifier.verifyErrorFreeLog();
        verifier.resetStreams();
    }

    /*
        run tests in test-app project (which intentionally fails), check handling
     */
    void goalTestApp() throws Exception {
        var verifier = newVerifier(testAppDir);
        try {
            verifier.executeGoals(List.of("kawa:test"));
            verifier.verifyErrorFreeLog();
            fail();
        } catch (VerificationException ignored) {
        }
        verifier.resetStreams();
    }

    /*
        run test with -DskipTests=true, check it doesn't run & fail
     */
    void goalTestAppSkipTestsTrue() throws Exception {
        var verifier = newVerifier(testAppDir);
        verifier.setSystemProperty("skipTests", "true");
        verifier.executeGoals(List.of("kawa:test"));
        verifier.verifyErrorFreeLog();
        verifier.resetStreams();
    }

    public void testMyPlugin() throws Exception {
        goalTestLib();
        goalReplServerLib();
        goalInstallLib();
        goalCompileApp();
        goalRunApp();
        goalTestApp();
        goalTestAppSkipTestsTrue();
    }
}
