package org.jenkinsci.plugins.SemanticVersioning;

import java.util.Map;

import hudson.EnvVars;
import hudson.model.AbstractBuild;
import hudson.model.EnvironmentContributingAction;

class InjectVersionVarsAction implements EnvironmentContributingAction {
	
	private Map<String,String> versions;
	
	public InjectVersionVarsAction(Map<String, String> versions) {
		super();
		this.versions = versions;
	}

	public String getIconFileName() {
		return null;
	}

	public String getDisplayName() {
		return Messages.DISPLAY_NAME;
	}

	public String getUrlName() {
		return null;
	}

	public void buildEnvVars(AbstractBuild<?, ?> build, EnvVars env) {
	    if (env == null) {
            return;
        }

        if (versions == null) {
            return;
        }

        for (Map.Entry<String, String> entry : versions.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (key != null && value != null) {
            	env.put(key, value);
            }
        }
	}
}