package org.jenkinsci.plugins.SemanticVersioning;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SemanticVersioningResult implements Serializable {

	private String version;
	private List<String> log;
	private Map<String,String> vars = new HashMap<String, String>();

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public List<String> getLog() {
		return log;
	}

	public void setLog(List<String> log) {
		this.log = log;
	}

	public Map<String,String> getVars() {
		return vars;
	}

	public void setVars(Map<String,String> vars) {
		this.vars = vars;
	}

}
