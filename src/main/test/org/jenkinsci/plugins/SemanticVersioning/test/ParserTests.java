package org.jenkinsci.plugins.SemanticVersioning.test;

import org.apache.commons.io.FileUtils;
import org.jenkinsci.plugins.SemanticVersioning.AppVersion;
import org.jenkinsci.plugins.SemanticVersioning.InvalidBuildFileFormatException;
import org.jenkinsci.plugins.SemanticVersioning.parsing.BuildDefinitionParser;
import org.junit.Test;
import org.jvnet.hudson.test.WithoutJenkins;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public abstract class ParserTests {

    protected final String version = "1.2.3-SNAPSHOT";

    @Test
    @WithoutJenkins
    public void testBuildFileNotFound() {
        System.out.println("####> testBuildFileNotFound");
        final String filename = "/non/existent/filename";
        try {
            BuildDefinitionParser buildParser = getParser();
            buildParser.extractAppVersion(filename);
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
        final String filename = "/tmp/InvalidBuild.test";
        try {
            generateInvalidBuildFile(filename);
            BuildDefinitionParser buildParser = getParser();
            buildParser.extractAppVersion(filename);
        } catch (InvalidBuildFileFormatException e) {
            assertEquals(getExpectedInvalidBuildFileFormatExceptionMessage(filename), e.getMessage());
        } catch (Exception e) {
            fail("InvalidBuildFileFormatException should have been thrown! Instead got: " + e);
        }
    }

    @Test
    @WithoutJenkins
    public void testValidBuildDefinitionWithMissingVersion() {
        System.out.println("####> testValidBuildDefinitionWithMissingVersion");
        final String filename = "/tmp/missingVersionBuild.test";
        try {
            generateBuildFileWithMissingVersion(filename);
            BuildDefinitionParser buildParser = getParser();
            buildParser.extractAppVersion(filename);
        } catch (InvalidBuildFileFormatException e) {
            assertEquals("No version information found in " + filename, e.getMessage());
        } catch (Exception e) {
            fail("InvalidBuildFileFormatException should have been thrown! Instead got: " + e);
        }
    }

    @Test
    @WithoutJenkins
    public void testBuildFileFoundAndValid() throws IOException, InvalidBuildFileFormatException {
        System.out.println("####> testBuildFileFoundAndValid");
        final String filename = "/tmp/Build.test";
        generateValidBuildFile(filename);
        BuildDefinitionParser buildParser = getParser();
        AppVersion parsedVersion = buildParser.extractAppVersion(filename);

        assertEquals(version, parsedVersion.toString());
    }

    protected abstract BuildDefinitionParser getParser();
    protected abstract void generateInvalidBuildFile(String filename) throws IOException;
    protected abstract void generateValidBuildFile(String filename) throws IOException;
    protected abstract void generateBuildFileWithMissingVersion(String filename) throws IOException;
    protected abstract String getExpectedInvalidBuildFileFormatExceptionMessage(String filename);

    protected void writeLinesToFile(String filename, Collection<String> lines) throws IOException {
        File file = new File(filename);
        if (file.exists()) {
            file.delete();
        }
        file.setWritable(true);

        FileUtils.writeLines(file, lines);
    }
}
