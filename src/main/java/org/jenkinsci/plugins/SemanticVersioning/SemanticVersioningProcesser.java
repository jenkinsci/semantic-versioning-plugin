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

package org.jenkinsci.plugins.SemanticVersioning;

import hudson.EnvVars;
import hudson.model.AbstractBuild;
import hudson.model.TaskListener;
import org.apache.commons.io.FileUtils;
import org.jenkinsci.plugins.SemanticVersioning.parsing.BuildDefinitionParser;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class SemanticVersioningProcesser {

    private static Logger logger = Logger.getLogger(String.valueOf(AppVersion.class));
    private AbstractBuild build;
    private BuildDefinitionParser parser;
    private boolean useJenkinsBuildNumber;
    private String semanticVersionFilename;

    public SemanticVersioningProcesser(
            AbstractBuild build,
            BuildDefinitionParser parser,
            boolean useJenkinsBuildNumber,
            String semanticVersionFilename) {
        this.build = build;
        this.parser = parser;
        this.useJenkinsBuildNumber = useJenkinsBuildNumber;
        this.semanticVersionFilename = semanticVersionFilename;
    }

    public AppVersion determineSemanticVersion() {
        AppVersion appVersion = getAppVersion();
        if(this.useJenkinsBuildNumber) {
            String buildNumber = getJenkinsBuildNumber();
            appVersion.setBuild(Integer.parseInt(buildNumber));
            logger.info("SemanticVersioningProcesser::getAppVersion -> using Jenkins Build Number: " + appVersion.toJsonString());
        }

        final String reportedVersion = appVersion.toString();
        writeVersionToFile(reportedVersion);

        return appVersion;
    }

    private void writeVersionToFile(String reportedVersion) {
        if (this.semanticVersionFilename != null && this.semanticVersionFilename.length() > 0) {
            String filename = this.build.getRootDir() + "/" + this.semanticVersionFilename;
            logger.info("SemanticVersioningProcesser::writeVersionToFile semantic version filename -> " + filename);
            File file = new File(filename);
            try {
                FileUtils.writeStringToFile(file, reportedVersion + "\n");
            } catch (IOException e) {
                logger.severe("Exception writing version to file: " + e);
                System.out.println(e);
            }
        }
    }

    private AppVersion getAppVersion() {
        AppVersion appVersion = AppVersion.EmptyVersion;
        if (this.parser != null) {
            try {
                logger.info("SemanticVersioningProcesser::getAppVersion -> attempting to parse using " + parser.getClass().getSimpleName());
                appVersion = parser.extractAppVersion(this.build);

            } catch (IOException e) {
                logger.severe("EXCEPTION: " + e);
            } catch (InvalidBuildFileFormatException e) {
                logger.severe("EXCEPTION: " + e);
            }
        }

        logger.info("SemanticVersioningProcesser::getAppVersion -> " + appVersion.toJsonString());

        return appVersion;
    }

    private String getJenkinsBuildNumber() {
        EnvVars environmentVariables = null;
        try {
            environmentVariables = this.build.getEnvironment(TaskListener.NULL);
        } catch (IOException e) {
            logger.severe("EXCEPTION: " + e);
        } catch (InterruptedException e) {
            logger.severe("EXCEPTION: " + e);
        }
        return environmentVariables != null ? environmentVariables.get(
                "BUILD_NUMBER", AppVersion.MISSING_BUILD_NUMBER) : AppVersion.MISSING_BUILD_NUMBER;
    }
}
