package org.jenkinsci.plugins.SemanticVersioning;

import hudson.EnvVars;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.TaskListener;
import hudson.tasks.BuildWrapper;
import hudson.tasks.BuildWrapperDescriptor;
import hudson.util.FormValidation;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.jenkinsci.plugins.SemanticVersioning.parsing.BuildScalaParser;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;

public class SemanticVersionBuildWrapper extends BuildWrapper {
    private static final String DEFAULT_ENVIRONMENT_VARIABLE_NAME = "SEMANTIC_APP_VERSION";
    private static final String MISSING_BUILD_NUMBER = "-1";
    private String environmentVariableName = DEFAULT_ENVIRONMENT_VARIABLE_NAME;
    private String semanticVersionFilename = ".semanticVersion";
    private static PrintStream logger = System.out;

    @DataBoundConstructor
    public SemanticVersionBuildWrapper(String environmentVariableName, String semanticVersionFilename) {
        System.out.println("### SemanticVersionBuildWrapper");
        this.environmentVariableName = environmentVariableName;
        this.semanticVersionFilename = semanticVersionFilename;
    }


    /**
     * Used from <tt>config.jelly</tt>.
     */
    public String getEnvironmentVariableName() {
        System.out.println("### SemanticVersionBuildWrapper::getEnvironmentVariableName");
        return this.environmentVariableName;
    }

    /**
     * Used from <tt>config.jelly</tt>.
     */
    public String getSemanticVersionFilename() {
        return semanticVersionFilename;
    }

    @Override
    public Environment setUp(AbstractBuild build, Launcher launcher, BuildListener listener) {
        setLogger(listener.getLogger());
        getLogger().println("### SemanticVersionBuildWrapper::setUp");
        AppVersion appVersion = getAppVersion(build, listener);
        String buildNumber = getJenkinsBuildNumber(build);

        appVersion.setBuild(Integer.parseInt(buildNumber));

        getLogger().println("appVersion found to be: {" + getEnvironmentVariableName() + ": " + appVersion + ", buildNumber: " + buildNumber + ", combined: " + appVersion.toString() + "}\n");

        final String reportedVersion = appVersion.toString();

        writeVersionToFile(build, listener, reportedVersion);

        return new Environment() {
            @Override
            public void buildEnvVars(Map<String, String> env) {
                env.put(getEnvironmentVariableName(), reportedVersion);
            }
        };
    }

    private void writeVersionToFile(AbstractBuild build, BuildListener listener, String reportedVersion) {
        String filename = getSemanticVersionFilename();
        if(filename != null && filename.length() > 0) {
            File file = new File(build.getWorkspace() + getSemanticVersionFilename());
            try {
                FileUtils.writeStringToFile(file, reportedVersion);
            } catch (IOException e) {
                getLogger().println("Exception writing version to file: " + e);
            }
        }
    }

    private AppVersion getAppVersion(AbstractBuild build, BuildListener listener) {
        getLogger().println("### SemanticVersionBuildWrapper::getAppVersion");
        AppVersion sbtAppVersion = AppVersion.EmptyVersion;
        String path = build.getWorkspace() + "/project/Build.scala";
        BuildScalaParser buildScalaParser = new BuildScalaParser();

        try {
            sbtAppVersion = buildScalaParser.extractAppVersion(path);
        } catch (IOException e) {
            getLogger().println("EXCEPTION: " + e);
        } catch (InvalidSbtBuildFileFormatException e) {
            getLogger().println("EXCEPTION: " + e);
        }
        return sbtAppVersion;
    }

    private String getJenkinsBuildNumber(AbstractBuild build) {
        getLogger().println("### SemanticVersionBuildWrapper::getJenkinsBuildNumber");
        EnvVars environmentVariables = null;
        try {
            environmentVariables = build.getEnvironment(TaskListener.NULL);
        } catch (IOException e) {
            getLogger().println("EXCEPTION: " + e);
        } catch (InterruptedException e) {
            getLogger().println("EXCEPTION: " + e);
        }
        return environmentVariables != null ? environmentVariables.get("BUILD_NUMBER", MISSING_BUILD_NUMBER) : MISSING_BUILD_NUMBER;
    }

    private static PrintStream getLogger() {
        return logger;
    }

    private void setLogger(PrintStream printStream) {
        logger = printStream;
    }

    @Extension
    public static final DescriptorImpl descriptor = new DescriptorImpl();

    @Override
    public BuildWrapperDescriptor getDescriptor() {
        return descriptor;
    }

    /**
     * descriptor for {@link SemanticVersionBuildWrapper}. Used as a singleton.
     * The class is marked as public so that it can be accessed from views.
     * <p/>
     * <p/>
     * See <tt>src/main/resources/hudson/plugins/hello_world/SbtVersionExtracter/*.jelly</tt>
     * for the actual HTML fragment for the configuration screen.
     */

    @Extension // This indicates to Jenkins that this is an implementation of an extension point.
    public static final class DescriptorImpl extends BuildWrapperDescriptor {

        /**
         * In order to load the persisted global configuration, you have to
         * call load() in the constructor.
         */
        public DescriptorImpl() {
            super(SemanticVersionBuildWrapper.class);
            getLogger().println("### DescriptorImpl");
            load();
        }

        /**
         * This human readable name is used in the configuration screen.
         */
        public String getDisplayName() {
            getLogger().println("### DescriptorImpl::getDisplayName");
            return "Determine Semantic Version for project.";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
            getLogger().println("### DescriptorImpl::configure");
            return super.configure(req, json);
        }

        @Override
        public boolean isApplicable(AbstractProject<?, ?> abstractProject) {
            getLogger().println("### DescriptorImpl::isApplicable");
            return true;
        }

        /**
         * Performs on-the-fly validation of the form field 'name'.
         *
         * @param value This parameter receives the value that the user has typed.
         * @return Indicates the outcome of the validation. This is sent to the browser.
         */
        public FormValidation doCheckEnvironmentVariableName(@QueryParameter String value) {
            getLogger().println("### DescriptorImpl::doCheckEnvironmentVariableName");
            if (value.length() == 0)
                return FormValidation.error("Please set a name");
            if (value.length() < 4)
                return FormValidation.warning("Isn't the name too short?");
            return FormValidation.ok();
        }

        public String getDefaultEnvironmentVariableName() {
            getLogger().println("### DescriptorImpl::getDefaultEnvironmentVariableName");
            return SemanticVersionBuildWrapper.DEFAULT_ENVIRONMENT_VARIABLE_NAME;
        }
    }
}
