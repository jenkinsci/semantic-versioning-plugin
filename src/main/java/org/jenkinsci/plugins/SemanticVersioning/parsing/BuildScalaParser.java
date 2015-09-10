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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.jenkinsci.plugins.SemanticVersioning.AppVersion;
import org.jenkinsci.plugins.SemanticVersioning.InvalidBuildFileFormatException;
import org.jenkinsci.plugins.SemanticVersioning.Messages;

import hudson.Extension;
import hudson.FilePath;
import hudson.model.Descriptor;

@Extension
public class BuildScalaParser extends AbstractBuildDefinitionParser {

    private static final String BUILD_DEFINITION_FILENAME = "/project/Build.scala";

    public BuildScalaParser() {   }

	public AppVersion extractAppVersion(FilePath workspace, PrintStream logger) throws InvalidBuildFileFormatException, IOException {
        String filename = workspace + BUILD_DEFINITION_FILENAME;
        File file = new File(filename);
        if(file.exists()) {
            Pattern extendsBuild = Pattern.compile(".*extends\\s+Build.*");
            String content = FileUtils.readFileToString(file);
            if(extendsBuild.matcher(content).find()) {
                String version;
                Pattern pattern = Pattern.compile("val\\s*appVersion\\s*=\\s*\"([^\"]*)\"", Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(content);
                boolean found = matcher.find();

                if(found) {
                    version = matcher.toMatchResult().group(1);
                } else {
                    throw new InvalidBuildFileFormatException("No version information found in " + BUILD_DEFINITION_FILENAME);
                }

                return AppVersion.parse(version);

            } else {
                throw new InvalidBuildFileFormatException("'" + BUILD_DEFINITION_FILENAME + "' is not a valid build definition file.");
            }

        } else {
            throw new FileNotFoundException("'" + BUILD_DEFINITION_FILENAME + "' was not found.");
        }
    }
	
	@SuppressWarnings("unchecked")
	public Descriptor<BuildDefinitionParser> getDescriptor() {
		return new AbstractSemanticParserDescription() {
			
			@Override
			public String getDisplayName() {
				
				return Messages.Parsers.SBT_BUILD_SCALA_PARSER;
			}
		};
	}
}
