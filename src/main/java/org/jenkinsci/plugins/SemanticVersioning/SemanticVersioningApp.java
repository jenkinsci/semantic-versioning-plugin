package org.jenkinsci.plugins.SemanticVersioning;

import hudson.EnvVars;
import hudson.model.AbstractBuild;
import hudson.model.TaskListener;
import org.apache.commons.io.FileUtils;
import org.jenkinsci.plugins.SemanticVersioning.parsing.BuildDefinitionParser;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class SemanticVersioningApp {

    private static Logger logger = Logger.getLogger(String.valueOf(AppVersion.class));
    private AbstractBuild build;
    private BuildDefinitionParser parser;
    private boolean useJenkinsBuildNumber;
    private String semanticVersionFilename;

    public SemanticVersioningApp(
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
            logger.info("### SemanticVersionBuildWrapper::getAppVersion -> using Jenkins Build Number: " + appVersion.toJsonString());
        }

        final String reportedVersion = appVersion.toString();
        writeVersionToFile(reportedVersion);

        return appVersion;
    }
    private void writeVersionToFile(String reportedVersion) {
        if (this.semanticVersionFilename != null && this.semanticVersionFilename.length() > 0) {
            File file = new File(this.build.getRootDir() + "/" + this.semanticVersionFilename);
            try {
                FileUtils.writeStringToFile(file, reportedVersion + "\n");
            } catch (IOException e) {
                logger.severe("Exception writing version to file: " + e);
            }
        }
    }

    private AppVersion getAppVersion() {
        AppVersion appVersion = AppVersion.EmptyVersion;
        if (this.parser != null) {
            try {
                logger.info("### SemanticVersionBuildWrapper::getAppVersion -> attempting to parse using " + parser.getClass().getSimpleName());
                appVersion = parser.extractAppVersion(this.build);

            } catch (IOException e) {
                logger.severe("EXCEPTION: " + e);
            } catch (InvalidBuildFileFormatException e) {
                logger.severe("EXCEPTION: " + e);
            }
        }

        logger.info("### SemanticVersionBuildWrapper::getAppVersion -> " + appVersion.toJsonString());

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
