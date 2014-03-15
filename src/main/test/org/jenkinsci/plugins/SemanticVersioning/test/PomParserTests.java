package org.jenkinsci.plugins.SemanticVersioning.test;

import org.jenkinsci.plugins.SemanticVersioning.parsing.BuildDefinitionParser;
import org.jenkinsci.plugins.SemanticVersioning.parsing.PomParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class PomParserTests extends ParserTests {

    @Override
    protected BuildDefinitionParser getParser() {
        return new PomParser();
    }

    @Override
    protected void generateInvalidBuildFile(String filename) throws IOException {
        Collection<String> lines = new ArrayList<String>();
        lines.add("<invalid></unvalid>");

        writeLinesToFile(filename, lines);
    }

    @Override
    protected void generateValidBuildFile(String filename) throws IOException {
        Collection<String> lines = new ArrayList<String>();
        lines.add("<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">");
        lines.add("<groupId>org.jenkins-ci.plugins</groupId>");
        lines.add("<artifactId>SemanticVersioning</artifactId>");
        lines.add("<version>" + version + "</version>");
        lines.add("<packaging>hpi</packaging>");
        lines.add("</project>");

        writeLinesToFile(filename, lines);
    }

    @Override
    protected void generateBuildFileWithMissingVersion(String filename) throws IOException {
        Collection<String> lines = new ArrayList<String>();
        lines.add("<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">");
        lines.add("<groupId>org.jenkins-ci.plugins</groupId>");
        lines.add("<artifactId>SemanticVersioning</artifactId>");
        lines.add("<packaging>hpi</packaging>");
        lines.add("</project>");

        writeLinesToFile(filename, lines);
    }

    @Override
    protected String getExpectedInvalidBuildFileFormatExceptionMessage(String filename) {
        return filename + " is not a valid POM file.";
    }
}
