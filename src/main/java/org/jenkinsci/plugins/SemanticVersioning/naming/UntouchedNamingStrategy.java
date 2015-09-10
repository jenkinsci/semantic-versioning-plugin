package org.jenkinsci.plugins.SemanticVersioning.naming;

import hudson.Extension;
import hudson.model.Descriptor;

import java.io.Serializable;
import java.util.Map;
import java.util.logging.Logger;

import org.jenkinsci.plugins.SemanticVersioning.AppVersion;
import org.jenkinsci.plugins.SemanticVersioning.Messages;
import org.jenkinsci.plugins.SemanticVersioning.parsing.AbstractSemanticParserDescription;

@Extension
public class UntouchedNamingStrategy implements NamingStrategy, Serializable {

    private static Logger logger = Logger.getLogger(String.valueOf(AppVersion.class));

    public Descriptor<NamingStrategy> getDescriptor() {
		return new AbstractSemanticParserDescription() {
			
			@Override
			public String getDisplayName() {
				
				return Messages.NamingStrategies.UNTOUCHED;
			}
		};
	}

	public String exportNames(AppVersion current, Map<String,String> vars, boolean useBuildNumber, int buildNumber) {
		logger.info("SemanticVersioningProcesser::getAppVersion -> maven naming: " +current.toJsonString());
		String releaseVersion = current.getOriginal();
		logger.info("SemanticVersioningProcesser::getAppVersion -> setting release version: " +releaseVersion);
		vars.put("releaseVersion", releaseVersion);
		return releaseVersion;
	}

}
