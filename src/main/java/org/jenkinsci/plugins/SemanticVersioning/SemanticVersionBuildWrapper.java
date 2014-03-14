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
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class SemanticVersionBuildWrapper extends BuildWrapper {
    private static final String DEFAULT_ENVIRONMENT_VARIABLE_NAME = "SEMANTIC_APP_VERSION";
    private static final String MISSING_BUILD_NUMBER = "-1";
    private String environmentVariableName = DEFAULT_ENVIRONMENT_VARIABLE_NAME;
    private String semanticVersionFilename = ".semanticVersion";

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
        System.out.println("### SemanticVersionBuildWrapper::setUp");
        String sbtAppVersion = getAppVersion(build, listener);
        String buildNumber = getJenkinsBuildNumber(build);

        AppVersion appVersion = AppVersion.parse(sbtAppVersion);
        appVersion.setBuild(Integer.parseInt(buildNumber));

        listener.getLogger().print("appVersion found to be: {" + getEnvironmentVariableName() + ": " + sbtAppVersion + ", buildNumber: " + buildNumber + ", combined: " + appVersion.toString() + "}\n");

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
                listener.getLogger().print("Exception writing version to file: " + e);
            }
        }
    }

    private String getAppVersion(AbstractBuild build, BuildListener listener) {
        System.out.println("### SemanticVersionBuildWrapper::getAppVersion");
        String sbtAppVersion = "";
        String path = build.getWorkspace() + "/project/Build.scala";

        try {
            sbtAppVersion = BuildScalaParser.extractAppVersion(path);
        } catch (IOException e) {
            listener.getLogger().print("EXCEPTION: " + e.getMessage() + "\n");
        } catch (InvalidSbtBuildFileFormatException e) {
            listener.getLogger().print("EXCEPTION: " + e.getMessage() + "\n");
        }
        return sbtAppVersion;
    }

    private String getJenkinsBuildNumber(AbstractBuild build) {
        System.out.println("### SemanticVersionBuildWrapper::getJenkinsBuildNumber");
        EnvVars environmentVariables = null;
        try {
            environmentVariables = build.getEnvironment(TaskListener.NULL);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return environmentVariables != null ? environmentVariables.get("BUILD_NUMBER", MISSING_BUILD_NUMBER) : MISSING_BUILD_NUMBER;
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
            System.out.println("### DescriptorImpl");
            load();
        }

        /**
         * This human readable name is used in the configuration screen.
         */
        public String getDisplayName() {
            System.out.println("### DescriptorImpl::getDisplayName");
            return "Determine Semantic Version for project.";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
            System.out.println("### DescriptorImpl::configure");
            return super.configure(req, json);
        }

        @Override
        public boolean isApplicable(AbstractProject<?, ?> abstractProject) {
            System.out.println("### DescriptorImpl::isApplicable");
            return true;
        }

        /**
         * Performs on-the-fly validation of the form field 'name'.
         *
         * @param value This parameter receives the value that the user has typed.
         * @return Indicates the outcome of the validation. This is sent to the browser.
         */
        public FormValidation doCheckEnvironmentVariableName(@QueryParameter String value) {
            System.out.println("### DescriptorImpl::doCheckEnvironmentVariableName");
            if (value.length() == 0)
                return FormValidation.error("Please set a name");
            if (value.length() < 4)
                return FormValidation.warning("Isn't the name too short?");
            return FormValidation.ok();
        }

        public String getDefaultEnvironmentVariableName() {
            System.out.println("### DescriptorImpl::getDefaultEnvironmentVariableName");
            return SemanticVersionBuildWrapper.DEFAULT_ENVIRONMENT_VARIABLE_NAME;
        }
    }

}
