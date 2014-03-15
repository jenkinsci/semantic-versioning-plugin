package org.jenkinsci.plugins.SemanticVersioning.parsing;

import org.jenkinsci.plugins.SemanticVersioning.AppVersion;
import org.jenkinsci.plugins.SemanticVersioning.InvalidSbtBuildFileFormatException;

import java.io.IOException;

public interface BuildDefinitionParser {
    AppVersion extractAppVersion(String filename) throws IOException, InvalidSbtBuildFileFormatException;
}
