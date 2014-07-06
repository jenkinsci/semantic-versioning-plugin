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

package org.jenkinsci.plugins.SemanticVersioning.columnDisplay;

import hudson.Extension;
import hudson.model.Descriptor;
import hudson.model.Job;
import hudson.model.Run;
import org.apache.commons.io.FileUtils;
import org.jenkinsci.plugins.SemanticVersioning.AppVersion;
import org.jenkinsci.plugins.SemanticVersioning.Messages;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

@Extension
public class LastSuccessfulBuildStrategy extends AbstractColumnDisplayStrategy {

    private static Logger logger = Logger.getLogger(String.valueOf(AppVersion.class));

    public String getDisplayString(Job job) {
        String semanticVersion = null;
        Run run = job.getLastSuccessfulBuild();
        if (run == null) {
            logger.info("LastSuccessfulBuildStrategy::getDisplayString -> last successful build not found.");
            semanticVersion = Messages.UNKNOWN_VERSION;
        } else {
            String filename = run.getRootDir() + "/" + Messages.SEMANTIC_VERSION_FILENAME;
            logger.info("LastSuccessfulBuildStrategy::getDisplayString -> last successful build found. Filename -> " + filename);
            File file = new File(filename);
            if (file.exists()) {
                try {
                    semanticVersion = FileUtils.readFileToString(file);
                    logger.info("LastSuccessfulBuildStrategy::getDisplayString -> read semantic version from file -> " + semanticVersion);
                } catch (IOException e) {
                    logger.severe(e.toString());
                }
            } else {
                logger.info("LastSuccessfulBuildStrategy::getDisplayString -> semanticVersion file not found.");
                semanticVersion = Messages.UNKNOWN_VERSION;
            }
        }

        return semanticVersion;
    }

    @SuppressWarnings("unchecked")
    public Descriptor<ColumnDisplayStrategy> getDescriptor() {
        return new AbstractDisplayStrategyDescription() {
            @Override
            public String getDisplayName() {
                return Messages.ColumnDisplayStrategies.LAST_SUCCESSFUL_BUILD_VERSION_STRATEGY;
            }
        };
    }
}
