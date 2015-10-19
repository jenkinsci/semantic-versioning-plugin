package org.jenkinsci.plugins.SemanticVersioning.naming;

import hudson.model.Describable;

import java.io.Serializable;
import java.util.Map;

import org.jenkinsci.plugins.SemanticVersioning.AppVersion;

public interface NamingStrategy extends Describable<NamingStrategy>, Serializable {
	
	public String exportNames(AppVersion current, Map<String,String> vars, boolean useBuildNumber, int buildNumber);
	

}
