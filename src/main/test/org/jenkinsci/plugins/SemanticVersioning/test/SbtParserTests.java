package org.jenkinsci.plugins.SemanticVersioning.test;

import org.jenkinsci.plugins.SemanticVersioning.parsing.BuildDefinitionParser;
import org.jenkinsci.plugins.SemanticVersioning.parsing.SbtParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class SbtParserTests extends ParserTests {

    @Override
    protected BuildDefinitionParser getParser(String filename) {
        return new SbtParser(filename);
    }

    @Override
    protected void generateInvalidBuildFile(String filename) throws IOException {
        Collection<String> lines = new ArrayList<String>();
        writeLinesToFile(filename, lines);
    }

    @Override
    protected void generateValidBuildFile(String filename) throws IOException {
        Collection<String> lines = new ArrayList<String>();
        lines.add("name := \"TestApp\"");
        lines.add("version := \"" + version + "\"");
        writeLinesToFile(filename, lines);
    }

    @Override
    protected void generateBuildFileWithMissingVersion(String filename) throws IOException {
        Collection<String> lines = new ArrayList<String>();
        lines.add("name := \"TestApp\"");
        writeLinesToFile(filename, lines);
    }

    @Override
    protected String getExpectedInvalidBuildFileFormatExceptionMessage(String filename) {
        return "'" + filename + "' is not a valid SBT build definition file.";
    }
}
