package org.jenkinsci.plugins.SemanticVersioning;

import hudson.FilePath;
import hudson.remoting.Callable;
import org.jenkinsci.plugins.SemanticVersioning.naming.NamingStrategy;
import org.jenkinsci.plugins.SemanticVersioning.parsing.BuildDefinitionParser;
import org.jenkinsci.remoting.RoleChecker;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SemanticVersioningCallable implements Callable<SemanticVersioningResult,IOException>, Serializable {

	private static final long serialVersionUID = -2239554739563636620L;

	private String env;
	private boolean useBuildNumber;
	
	private FilePath workspace;
	private int buildNumber;
	
	private BuildDefinitionParser parser;
	private NamingStrategy namingStrategy;
	

    
    public SemanticVersioningCallable() {
	}
	
	@Override
	public void checkRoles(RoleChecker arg0) throws SecurityException {
	}

	@Override
	public SemanticVersioningResult call() throws IOException {
		
		SemanticVersioningResult out = new SemanticVersioningResult();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos,true,"utf-8");
		
		ps.println("executing on "+InetAddress.getLocalHost().getHostName());
		
		try {
	        AppVersion appVersion = AppVersion.EmptyVersion;
	        
	        try {
	            appVersion = getAppVersion(ps);
				
	            ps.println("exporting vars ... ");
	            Map<String, String> vars = new HashMap<String, String>();
	            String v = namingStrategy.exportNames(appVersion, vars, useBuildNumber, buildNumber);

	            out.setVersion(v);
	            
			} catch (Exception e) {
				throw new IOException(e);
			}
	        
	        
	        Map<String,String> vars = new HashMap<String,String>();
	        namingStrategy.exportNames(appVersion,vars,useBuildNumber,buildNumber);
	        
	        out.setVars(vars);
	        
		} catch (Exception e) {
			e.printStackTrace(ps);
		}
		
		ps.flush();
		baos.flush();
		
		List<String> log = new ArrayList<String>();
		for(String l : new String(baos.toByteArray(),"utf-8").split("[\\r\\n]+")) {
			log.add(l);
		}
		
		out.setLog(log);
		
		return out;
	}

    private AppVersion getAppVersion(PrintStream log) throws IOException {
        AppVersion appVersion = AppVersion.EmptyVersion;
        if (this.parser != null) {
            try {
                appVersion = parser.extractAppVersion(this.getWorkspace(),log);
            } catch (Exception e) {
            	throw new IOException(e);
            }
        }
        return appVersion;
    }

	public String getEnv() {
		return env;
	}

	public void setEnv(String env) {
		this.env = env;
	}

	public BuildDefinitionParser getParser() {
		return parser;
	}

	public void setParser(BuildDefinitionParser parser) {
		this.parser = parser;
	}

	public NamingStrategy getNamingStrategy() {
		return namingStrategy;
	}

	public void setNamingStrategy(NamingStrategy namingStrategy) {
		this.namingStrategy = namingStrategy;
	}

	public boolean isUseBuildNumber() {
		return useBuildNumber;
	}

	public void setUseBuildNumber(boolean useBuildNumber) {
		this.useBuildNumber = useBuildNumber;
	}

	public FilePath getWorkspace() {
		return workspace;
	}

	public void setWorkspace(FilePath workspace) {
		this.workspace = workspace;
	}

	public int getBuildNumber() {
		return buildNumber;
	}

	public void setBuildNumber(int buildNumber) {
		this.buildNumber = buildNumber;
	}

}
