package org.jenkinsci.plugins.SemanticVersioning.test;

import org.jenkinsci.plugins.SemanticVersioning.parsing.BuildDefinitionParser;
import org.jenkinsci.plugins.SemanticVersioning.parsing.BuildScalaParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class BuildScalaParserTests extends ParserTests {

    @Override
    protected BuildDefinitionParser getParser() {
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
