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

package org.jenkinsci.plugins.SemanticVersioning.parsing;

import org.apache.commons.io.FileUtils;
import org.jenkinsci.plugins.SemanticVersioning.AppVersion;
import org.jenkinsci.plugins.SemanticVersioning.InvalidSbtBuildFileFormatException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BuildScalaParser implements BuildDefinitionParser {

    public AppVersion extractAppVersion(String filename) throws InvalidSbtBuildFileFormatException, IOException {
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

                return AppVersion.parse(version);

            } else {
                throw new InvalidSbtBuildFileFormatException("'" + filename + "' is not a valid SBT Build file.");
            }

        } else {
            throw new FileNotFoundException("'" + filename + "' was not found.");
        }
    }
}
