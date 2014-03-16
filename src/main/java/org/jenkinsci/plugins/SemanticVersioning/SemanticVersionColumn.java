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
import hudson.Extension;
import hudson.model.AbstractItem;
import hudson.model.Descriptor;
import hudson.model.Job;
import hudson.model.Run;
import hudson.views.ListViewColumn;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kohsuke.stapler.StaplerRequest;

import java.io.File;
import java.io.IOException;

public class SemanticVersionColumn extends ListViewColumn {

    public static final String UNKNOWN_VERSION = "Unknown";
    public static final String SEMANTIC_VERSION_COLUMN_DISPLAY_NAME = "Semantic Version";
    public static final String SEMANTIC_VERSION_FILENAME = "/.semanticVersion";
    private static Logger logger = LogManager.getLogger(AppVersion.class);

    @Extension
    public static final Descriptor<ListViewColumn> descriptor = new DescriptorImpl();

    public String getSemanticVersion(String semver, Job job) throws IOException, InterruptedException {

        String semanticVersion = semver;

        AbstractItem abstractItem = job;
        logger.debug("Job simple name -> " + abstractItem.getClass().getSimpleName());
        logger.debug("Job pronoun -> " + abstractItem.getPronoun());

        if(semanticVersion == null || semanticVersion.length() == 0) {

            Run run = job.getLastSuccessfulBuild();
            if(run == null) {
                logger.warn("Last Successful Build not found");
                semanticVersion = UNKNOWN_VERSION;
            } else {
                File file = new File(run.getArtifactsDir() + SEMANTIC_VERSION_FILENAME);
                if(file.exists()) {
                    try {
                        logger.debug("Reading Semantic Version from: " + file.getAbsolutePath());
                        semanticVersion = FileUtils.readFileToString(file);
                    } catch (IOException e) {
                        logger.error(e);
                    }
                } else {
                    semanticVersion = UNKNOWN_VERSION;
                }
            }
        }

        return semanticVersion;
    }

    private static class DescriptorImpl extends Descriptor<ListViewColumn> {
        @Override
        public ListViewColumn newInstance(StaplerRequest req, JSONObject formData) throws FormException {
            return new SemanticVersionColumn();
        }

        @Override
        public String getDisplayName() {
            return SEMANTIC_VERSION_COLUMN_DISPLAY_NAME;
        }
    }
}
