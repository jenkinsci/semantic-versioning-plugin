/*
 * The MIT License
 *
 * Copyright (c) 2014, Arne M. Størksen
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

package org.jenkinsci.plugins.SemanticVersioning;

import org.jenkinsci.plugins.SemanticVersioning.parsing.BuildDefinitionParser;
import org.jenkinsci.plugins.SemanticVersioning.parsing.BuildScalaParser;
import org.jenkinsci.plugins.SemanticVersioning.test.ParserTests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class BowerParserTests extends ParserTests {

    @Override
    protected BuildDefinitionParser getParser(String filename) {
        return new BuildScalaParser();
    }

    @Override
    protected void generateInvalidBuildFile(String filename) throws IOException {
        Collection<String> lines = new ArrayList<String>();
        lines.add("This is an invalid build file.");

        writeLinesToFile(filename, lines);
    }

    @Override
    protected void generateValidBuildFile(String filename) throws IOException {
        Collection<String> lines = new ArrayList<String>();
        lines.add("object ApplicationBuild extends Build {\n");
        lines.add("val neo4jVersion          = \"1.9.2\"\n");
        lines.add("val appName         = \"atomicsteampunk\"\n");
        lines.add("val appVersion      = \"" + version + "\"");
        lines.add("}\n");

        writeLinesToFile(filename, lines);
    }

    @Override
    protected void generateBuildFileWithMissingVersion(String filename) throws IOException {
        Collection<String> lines = new ArrayList<String>();
        lines.add("object ApplicationBuild extends Build {\n");
        lines.add("val neo4jVersion          = \"1.9.2\"\n");
        lines.add("val appName         = \"atomicsteampunk\"\n");
        lines.add("}\n");

        writeLinesToFile(filename, lines);
    }

    @Override
    protected String getExpectedInvalidBuildFileFormatExceptionMessage(String filename) {
        return "'" + filename + "' is not a valid build definition file.";
    }
}
