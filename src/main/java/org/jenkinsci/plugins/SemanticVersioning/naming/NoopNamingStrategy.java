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
public class NoopNamingStrategy implements NamingStrategy, Serializable {

    private static Logger logger = Logger.getLogger(String.valueOf(AppVersion.class));

    public Descriptor<NamingStrategy> getDescriptor() {
		return new AbstractSemanticParserDescription() {
			
			@Override
			public String getDisplayName() {
				
				return Messages.NamingStrategies.NOOP_NAMING;
			}
		};
	}

	public String exportNames(AppVersion current, Map<String,String> vars, boolean useBuildNumber, int buildNumber) {
		// it's called "noop", d'oh!
        logger.info("SemanticVersioningProcesser::getAppVersion -> not setting anything (NOOP): " + current.toJsonString());
        return current.toString();
	}

}
