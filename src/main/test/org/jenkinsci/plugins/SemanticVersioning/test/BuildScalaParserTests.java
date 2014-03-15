package org.jenkinsci.plugins.SemanticVersioning.test;

import org.apache.commons.io.FileUtils;
import org.jenkinsci.plugins.SemanticVersioning.AppVersion;
import org.jenkinsci.plugins.SemanticVersioning.parsing.BuildScalaParser;
import org.jenkinsci.plugins.SemanticVersioning.InvalidSbtBuildFileFormatException;
import org.junit.Test;
import org.jvnet.hudson.test.WithoutJenkins;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class BuildScalaParserTests {

    private final String version = "1.2.3-SNAPSHOT";

    @Test
    @WithoutJenkins
    public void testBuildFileNotFound() {
        System.out.println("####> testBuildFileNotFound");
        final String filename = "/non/existent/filename";
        try {
            BuildScalaParser buildScalaParser = new BuildScalaParser();
            buildScalaParser.extractAppVersion(filename);
            fail("FileNotFoundException should have been thrown!");
        } catch (FileNotFoundException e) {
            assertEquals("'" + filename + "' was not found.", e.getMessage());
        } catch (Exception e) {
            fail("FileNotFoundException should have been thrown! Instead got: " + e);
        }
    }

    @Test
    @WithoutJenkins
    public void testBuildFileFoundNotValidBuildFile() {
        System.out.println("####> testBuildFileFoundNotValidBuildFile");
        final String filename = "/tmp/InvalidBuild.scala";
        try {
            generateInvalidBuildFile(filename);
            BuildScalaParser buildScalaParser = new BuildScalaParser();
            buildScalaParser.extractAppVersion(filename);
        } catch (InvalidSbtBuildFileFormatException e) {
            assertEquals("'" + filename + "' is not a valid SBT Build file.", e.getMessage());
        } catch (Exception e) {
            fail("FileNotFoundException should have been thrown! Instead got: " + e);
        }
    }

    @Test
    @WithoutJenkins
    public void testBuildFileFoundAndValid() throws IOException, InvalidSbtBuildFileFormatException {
        System.out.println("####> testBuildFileFoundAndValid");
        final String filename = "/tmp/Build.scala";
        generateValidBuildFile(filename);
        BuildScalaParser buildScalaParser = new BuildScalaParser();
        AppVersion parsedVersion = buildScalaParser.extractAppVersion(filename);

        assertEquals(version, parsedVersion.toString());
    }

    private void generateInvalidBuildFile(String filename) throws IOException {
        File file = new File(filename);
        if (file.exists()) {
            file.delete();
        }
        file.setWritable(true);
        FileUtils.writeStringToFile(file, "This is an invalid build file.\n");
    }

    private void generateValidBuildFile(String filename) throws IOException {
        File file = new File(filename);
        if (file.exists()) {
            file.delete();
        }
        file.setWritable(true);

        Collection<String> fileLines = new ArrayList<String>();
        fileLines.add("object ApplicationBuild extends Build {\n");
        fileLines.add("val neo4jVersion          = \"1.9.2\"\n");
        fileLines.add("val appName         = \"atomicsteampunk\"\n");
        fileLines.add("val appVersion      = \"" + version + "\"");
        fileLines.add("}\n");

        FileUtils.writeLines(file, fileLines);
    }
}
