/*
 * The MIT License
 *
 * Copyright (c) 2014, Steve Wagner
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.jenkinsci.plugins.SemanticVersioning.test;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
    protected final Logger logger = LogManager.getLogger();

    @Test
    @WithoutJenkins
    public void testBuildFileNotFound() {
        logger.debug("####> testBuildFileNotFound");
        final String filename = "/non/existent/filename";
        try {
            BuildDefinitionParser buildParser = getParser(filename);
            buildParser.extractAppVersion();
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
        logger.debug("####> testBuildFileFoundNotValidBuildFile");
        final String filename = "/tmp/InvalidBuild.test";
        try {
            generateInvalidBuildFile(filename);
            BuildDefinitionParser buildParser = getParser(filename);
            buildParser.extractAppVersion();
        } catch (InvalidBuildFileFormatException e) {
            assertEquals(getExpectedInvalidBuildFileFormatExceptionMessage(filename), e.getMessage());
        } catch (Exception e) {
            fail("InvalidBuildFileFormatException should have been thrown! Instead got: " + e);
        }
    }

    @Test
    @WithoutJenkins
    public void testValidBuildDefinitionWithMissingVersion() {
        logger.debug("####> testValidBuildDefinitionWithMissingVersion");
        final String filename = "/tmp/missingVersionBuild.test";
        try {
            generateBuildFileWithMissingVersion(filename);
            BuildDefinitionParser buildParser = getParser(filename);
            buildParser.extractAppVersion();
        } catch (InvalidBuildFileFormatException e) {
            assertEquals("No version information found in " + filename, e.getMessage());
        } catch (Exception e) {
            fail("InvalidBuildFileFormatException should have been thrown! Instead got: " + e);
        }
    }

    @Test
    @WithoutJenkins
    public void testBuildFileFoundAndValid() throws IOException, InvalidBuildFileFormatException {
        logger.debug("####> testBuildFileFoundAndValid");
        final String filename = "/tmp/Build.test";
        generateValidBuildFile(filename);
        BuildDefinitionParser buildParser = getParser(filename);
        AppVersion parsedVersion = buildParser.extractAppVersion();

        assertEquals(version, parsedVersion.toString());
    }

    protected abstract BuildDefinitionParser getParser(String filename);
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
