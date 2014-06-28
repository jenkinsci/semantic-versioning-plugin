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
import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.BuildWrapper;
import hudson.tasks.BuildWrapperDescriptor;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.jenkinsci.plugins.SemanticVersioning.parsing.BuildDefinitionParser;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

public class SemanticVersionBuildWrapper extends BuildWrapper {
    private static final String DEFAULT_ENVIRONMENT_VARIABLE_NAME = "SEMANTIC_APP_VERSION";
	private static final String MISSING_BUILD_NUMBER = "-1";
    public static final String SEMANTIC_VERSION_PLUGIN_DISPLAY_NAME = "Determine Semantic Version for project";
    private String environmentVariableName = DEFAULT_ENVIRONMENT_VARIABLE_NAME;
	private static Logger logger = Logger.getLogger(String  .valueOf(AppVersion.class));
	private BuildDefinitionParser parser;
    private boolean useJenkinsBuildNumber = true;

	@DataBoundConstructor
	public SemanticVersionBuildWrapper(String environmentVariableName, String parser, boolean useJenkinsBuildNumber) {
		this.environmentVariableName = environmentVariableName;
        this.useJenkinsBuildNumber = useJenkinsBuildNumber;
		try {
			this.parser = (BuildDefinitionParser)Jenkins.getInstance()
					.getExtensionList(parser).iterator().next();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Used from <tt>config.jelly</tt>.
	 * 
	 * @return the value of the environment variable name to be used.
	 */
	public String getEnvironmentVariableName() { return this.environmentVariableName; }

	/**
	 * Used from <tt>config.jelly</tt>.
	 * 
	 * @return the name of the file in which the semantic version will be
	 *         stored.
	 */
	public String getSemanticVersionFilename() {
		return ".semanticVersion";
	}

    public boolean getUseJenkinsBuildNumber() { return this.useJenkinsBuildNumber; }
	
	/**
	 * Used from <tt>config.jelly</tt>.
	 * 
	 * @return the canonical class name of the parser  which the semantic version use
	 * to parse version number
	 */
	public String getParser() {
		return this.parser.getClass().getCanonicalName();
	}

	@Override
	public Environment setUp(AbstractBuild build, Launcher launcher, BuildListener listener) {
		AppVersion appVersion = getAppVersion(build);
        if(useJenkinsBuildNumber) {
            String buildNumber = getJenkinsBuildNumber(build);
            appVersion.setBuild(Integer.parseInt(buildNumber));
            logger.info("### SemanticVersionBuildWrapper::getAppVersion -> using Jenkins Build Number: " + appVersion.toJsonString());
        }

		final String reportedVersion = appVersion.toString();
		writeVersionToFile(build, reportedVersion);

		return new Environment() {
			@Override
			public void buildEnvVars(Map<String, String> env) {
				env.put(getEnvironmentVariableName(), reportedVersion);
			}
		};
	}

	private void writeVersionToFile(AbstractBuild build, String reportedVersion) {
		String filename = getSemanticVersionFilename();
		if (filename != null && filename.length() > 0) {
			File file = new File(build.getArtifactsDir() + "/" + filename);
			try {
				FileUtils.writeStringToFile(file, reportedVersion + "\n");
			} catch (IOException e) {
				logger.severe("Exception writing version to file: " + e);
			}
		}
	}

	private AppVersion getAppVersion(AbstractBuild build) {
		AppVersion appVersion = AppVersion.EmptyVersion;
		if (this.parser != null) {
			try {
				logger.info("### SemanticVersionBuildWrapper::getAppVersion -> attempting to parse using " + parser.getClass().getSimpleName());
                appVersion = parser.extractAppVersion(build);

			} catch (IOException e) {
				logger.severe("EXCEPTION: " + e);
			} catch (InvalidBuildFileFormatException e) {
				logger.severe("EXCEPTION: " + e);
			}
		}

        logger.info("### SemanticVersionBuildWrapper::getAppVersion -> " + appVersion.toJsonString());

        return appVersion;
	}

	private String getJenkinsBuildNumber(AbstractBuild build) {
		EnvVars environmentVariables = null;
		try {
			environmentVariables = build.getEnvironment(TaskListener.NULL);
		} catch (IOException e) {
			logger.severe("EXCEPTION: " + e);
		} catch (InterruptedException e) {
			logger.severe("EXCEPTION: " + e);
		}
		return environmentVariables != null ? environmentVariables.get(
				"BUILD_NUMBER", MISSING_BUILD_NUMBER) : MISSING_BUILD_NUMBER;
	}

	@Extension
	public static final DescriptorImpl descriptor = new DescriptorImpl();

	@Override
	public BuildWrapperDescriptor getDescriptor() {
		return descriptor;
	}

	/**
	 * descriptor for {@link SemanticVersionBuildWrapper}. Used as a singleton.
	 * The class is marked as public so that it can be accessed from views. See
	 * <tt>src/main/resources/hudson/plugins/hello_world/SbtVersionExtracter/*.jelly</tt>
	 * for the actual HTML fragment for the configuration screen.
	 */
	public static final class DescriptorImpl extends BuildWrapperDescriptor {

		/**
		 * In order to load the persisted global configuration, you have to call
		 * load() in the constructor.
		 */
		public DescriptorImpl() {
			super(SemanticVersionBuildWrapper.class);
			load();
		}

		/**
		 * This human readable name is used in the configuration screen.
		 * 
		 * @return the display name for the plugin
		 */
		public String getDisplayName() { return SEMANTIC_VERSION_PLUGIN_DISPLAY_NAME; }

		@Override
		public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
			return super.configure(req, json);
		}

		@Override
		public boolean isApplicable(AbstractProject<?, ?> abstractProject) {
			return true;
		}

		/**
		 * Performs on-the-fly validation of the form field 'name'.
		 * 
		 * @param value
		 *            This parameter receives the value that the user has typed.
		 * @return Indicates the outcome of the validation. This is sent to the
		 *         browser.
		 */
		public FormValidation doCheckEnvironmentVariableName(@QueryParameter String value) {
			if (value.isEmpty())
				return FormValidation.error("Please set a name");
			if (value.length() < 4)
				return FormValidation.warning("Isn't the name too short?");
			return FormValidation.ok();
		}

		/**
		 * Generates ListBoxModel for available BuildDefinitionParsers
		 * 
		 * @return available BuildDefinitionParsers as ListBoxModel
		 */
		public ListBoxModel doFillParserItems() {
			ListBoxModel parsersModel = new ListBoxModel();
			for (BuildDefinitionParser parser : Jenkins.getInstance()
					.getExtensionList(BuildDefinitionParser.class)) {
				parsersModel.add(parser.getDescriptor().getDisplayName(), parser
						.getClass().getCanonicalName());
			}

			return parsersModel;
		}

		/**
		 * Gets the default value for the environment variable name.
		 * 
		 * @return the default value for the environment variable name.
		 */
		public String getDefaultEnvironmentVariableName() {
			return SemanticVersionBuildWrapper.DEFAULT_ENVIRONMENT_VARIABLE_NAME;
		}

        /**
         * Gets the default value for the checkbox. The default is to use the Jenkins build number
         * instead of the build number in the parsed file.
         * @return true
         */
        public boolean getDefaultUseJenkinsBuildNumber() { return true; }
	}
}
