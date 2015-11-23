package org.jenkinsci.plugins.SemanticVersioning.naming;

import java.io.Serializable;
import java.util.Map;
import java.util.logging.Logger;

import hudson.Extension;
import hudson.model.Descriptor;

import org.jenkinsci.plugins.SemanticVersioning.AppVersion;
import org.jenkinsci.plugins.SemanticVersioning.Messages;
import org.jenkinsci.plugins.SemanticVersioning.parsing.AbstractSemanticParserDescription;

@Extension
public class MavenReleaseNamingStrategy implements NamingStrategy, Serializable {

    private static Logger logger = Logger.getLogger(String.valueOf(AppVersion.class));

    public Descriptor<NamingStrategy> getDescriptor() {
		return new AbstractSemanticParserDescription() {
			
			@Override
			public String getDisplayName() {
				
				return Messages.NamingStrategies.MAVEN_RELEASE_DEVELOPMENT;
			}
		};
	}

	public String exportNames(AppVersion current, Map<String,String> vars, boolean useBuildNumber, int buildNumber) {
		logger.info("SemanticVersioningProcessor::getAppVersion -> maven naming: " +current.toJsonString());
		String releaseVersion = current.getMajor()+"."+current.getMinor()+(useBuildNumber?"."+buildNumber:"");
		String developmentVersion = current.getMajor()+"."+(useBuildNumber?current.getMinor()+"."+(buildNumber+1):""+(current.getMinor()+1))+"-SNAPSHOT";
		logger.info("SemanticVersioningProcessor::getAppVersion -> setting release version: " +releaseVersion);
		logger.info("SemanticVersioningProcessor::getAppVersion -> setting development version: " +developmentVersion);
		vars.put("releaseVersion", releaseVersion);
		vars.put("developmentVersion", developmentVersion);
		return releaseVersion;
	}

}
