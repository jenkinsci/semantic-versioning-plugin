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
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.TaskListener;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Descriptor.FormException;
import hudson.tasks.BuildWrapper;
import hudson.tasks.BuildWrapperDescriptor;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import jenkins.model.Jenkins;
import net.sf.json.JSONObject;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jenkinsci.plugins.SemanticVersioning.parsing.BuildDefinitionParser;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

public class SemanticVersionBuildWrapper extends BuildWrapper {
	private static final String DEFAULT_ENVIRONMENT_VARIABLE_NAME = "SEMANTIC_APP_VERSION";
	private static final String MISSING_BUILD_NUMBER = "-1";
	private String environmentVariableName = DEFAULT_ENVIRONMENT_VARIABLE_NAME;
	private static Logger logger = LogManager.getLogger(AppVersion.class);
	private BuildDefinitionParser parser;

	@DataBoundConstructor
	public SemanticVersionBuildWrapper(String environmentVariableName,
			String parser) {
		logger.info("### SemanticVersionBuildWrapper");
		this.environmentVariableName = environmentVariableName;
		try {
			this.parser = (BuildDefinitionParser) Jenkins.getInstance()
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
	public String getEnvironmentVariableName() {
		logger.info("### SemanticVersionBuildWrapper::getEnvironmentVariableName");
		return this.environmentVariableName;
	}

	/**
	 * Used from <tt>config.jelly</tt>.
	 * 
	 * @return the name of the file in which the semantic version will be
	 *         stored.
	 */
	public String getSemanticVersionFilename() {
		return ".semanticVersion";
	}
	
	
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
	public Environment setUp(AbstractBuild build, Launcher launcher,
			BuildListener listener) {
		logger.debug("### SemanticVersionBuildWrapper::setUp");
		AppVersion appVersion = getAppVersion(build);
		String buildNumber = getJenkinsBuildNumber(build);

		appVersion.setBuild(Integer.parseInt(buildNumber));

		logger.debug("### SemanticVersionBuildWrapper::setUp -> appVersion found to be: {"
				+ getEnvironmentVariableName()
				+ ": "
				+ appVersion.getOriginal()
				+ ", buildNumber: "
				+ buildNumber
				+ ", combined: " + appVersion.toString() + "}\n");

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
			logger.info(build.getArtifactsDir() + "/" + filename);
			try {
				FileUtils.writeStringToFile(file, reportedVersion + "\n");
			} catch (IOException e) {
				logger.debug("Exception writing version to file: " + e);
			}
		}
	}

	private AppVersion getAppVersion(AbstractBuild build) {
		logger.debug("### SemanticVersionBuildWrapper::getAppVersion");
		AppVersion appVersion = AppVersion.EmptyVersion;
		if (this.parser != null) {
			try {
				logger.info("### SemanticVersionBuildWrapper::getAppVersion -> attempting to parse using "
						+ parser.getClass().getSimpleName());
				return parser.extractAppVersion(build);

			} catch (IOException e) {
				logger.error("EXCEPTION: " + e);
			} catch (InvalidBuildFileFormatException e) {
				logger.error("EXCEPTION: " + e);
			}
		}

		return appVersion;
	}

	private String getJenkinsBuildNumber(AbstractBuild build) {
		logger.debug("### SemanticVersionBuildWrapper::getJenkinsBuildNumber");
		EnvVars environmentVariables = null;
		try {
			environmentVariables = build.getEnvironment(TaskListener.NULL);
		} catch (IOException e) {
			logger.error("EXCEPTION: " + e);
		} catch (InterruptedException e) {
			logger.error("EXCEPTION: " + e);
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
			logger.debug("### DescriptorImpl");
			load();
		}

		/**
		 * This human readable name is used in the configuration screen.
		 * 
		 * @return the display name for the plugin
		 */
		public String getDisplayName() {
			logger.debug("### DescriptorImpl::getDisplayName");
			return "Determine Semantic Version for project";
		}

		@Override
		public boolean configure(StaplerRequest req, JSONObject json)
				throws FormException {
			logger.debug("### DescriptorImpl::configure");
			
			return super.configure(req, json);
		}

		@Override
		public boolean isApplicable(AbstractProject<?, ?> abstractProject) {
			logger.debug("### DescriptorImpl::isApplicable");
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
		public FormValidation doCheckEnvironmentVariableName(
				@QueryParameter String value) {
			logger.debug("### DescriptorImpl::doCheckEnvironmentVariableName");
			if (value.isEmpty())
				return FormValidation.error("Please set a name");
			if (value.length() < 4)
				return FormValidation.warning("Isn't the name too short?");
			return FormValidation.ok();
		}

		/**
		 * Generates LisBoxModel for available BuildDefinitionParsers
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
			logger.debug("### DescriptorImpl::getDefaultEnvironmentVariableName");
			return SemanticVersionBuildWrapper.DEFAULT_ENVIRONMENT_VARIABLE_NAME;
		}
	}
}
