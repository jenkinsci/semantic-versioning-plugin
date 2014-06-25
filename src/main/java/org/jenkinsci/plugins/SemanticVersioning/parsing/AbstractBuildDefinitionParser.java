package org.jenkinsci.plugins.SemanticVersioning.parsing;

import hudson.ExtensionList;
import hudson.model.Describable;
import jenkins.model.Jenkins;

import org.apache.tools.ant.ExtensionPoint;

/**
 * BuilDefinitionParser abstraction layer for better backward compatibility
 * @author timii
 *
 */

public abstract class AbstractBuildDefinitionParser extends ExtensionPoint implements BuildDefinitionParser {

	public static ExtensionList<BuildDefinitionParser> getParsers() {
			return Jenkins.getInstance().getExtensionList(BuildDefinitionParser.class);
	}
	
}
