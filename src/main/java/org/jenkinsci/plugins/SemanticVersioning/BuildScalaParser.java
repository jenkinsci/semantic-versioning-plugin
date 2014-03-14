package org.jenkinsci.plugins.SemanticVersioning;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BuildScalaParser {

    public static String extractAppVersion(String filename) throws IOException, InvalidSbtBuildFileFormatException {
        File file = new File(filename);
        if(file.exists()) {

            Pattern extendsBuild = Pattern.compile(".*extends\\s+Build.*");
            String content = FileUtils.readFileToString(file);
            if(extendsBuild.matcher(content).find()) {
                String version = "NOT FOUND";
                Pattern pattern = Pattern.compile("val\\s*appVersion\\s*=\\s*\"([^\"]*)\"", Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(content);
                boolean found = matcher.find();

                if(found) {
                    version = matcher.toMatchResult().group(1);
                }

                return version;

            } else {
                throw new InvalidSbtBuildFileFormatException("'" + filename + "' is not a valid SBT Build file.");
            }

        } else {
            throw new FileNotFoundException("'" + filename + "' was not found.");
        }
    }
}
