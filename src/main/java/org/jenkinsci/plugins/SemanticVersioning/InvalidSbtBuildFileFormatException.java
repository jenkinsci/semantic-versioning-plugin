package org.jenkinsci.plugins.SemanticVersioning;

public class InvalidSbtBuildFileFormatException extends Exception {
    public InvalidSbtBuildFileFormatException(String message) {
        super(message);
    }
}
