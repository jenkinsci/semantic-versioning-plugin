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

package org.jenkinsci.plugins.SemanticVersioning.parsing;

import hudson.Extension;
import hudson.FilePath;
import hudson.FilePath.FileCallable;
import hudson.maven.AbstractMavenBuild;
import hudson.maven.MavenBuild;
import hudson.maven.MavenModuleSet;
import hudson.model.AbstractBuild;
import hudson.model.Descriptor;
import hudson.remoting.VirtualChannel;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import jenkins.model.Jenkins;

import org.jenkinsci.plugins.SemanticVersioning.AbstractSematicParserDescription;
import org.jenkinsci.plugins.SemanticVersioning.AppVersion;
import org.jenkinsci.plugins.SemanticVersioning.InvalidBuildFileFormatException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

@Extension
public class PomParser extends AbstractBuildDefinitionParser {

	private static final String BUILD_FILE = "pom.xml";

	public PomParser() {
	}

	@Deprecated
	public PomParser(String filename) {
	}

	public AppVersion extractAppVersion(AbstractBuild<?, ?> build)
			throws IOException, InvalidBuildFileFormatException {
		String version = null;

		Document document = getPom((AbstractMavenBuild<?, ?>) build);
		XPath xPath = XPathFactory.newInstance().newXPath();
		XPathExpression expression;
		try {
			expression = xPath.compile("/project/version");
			version = expression.evaluate(document);

		} catch (XPathExpressionException e) {
			throw new InvalidBuildFileFormatException(document.getBaseURI()
					+ " is not a valid POM file.");
		}

		if (version == null || version.length() == 0) {
			throw new InvalidBuildFileFormatException(
					"No version information found in " + document.getBaseURI());
		}
		return AppVersion.parse(version);
	}

	private Document getPom(AbstractMavenBuild<?, ?> mavenBuild)
			throws InvalidBuildFileFormatException, IOException {
		FilePath moduleRoot = mavenBuild.getModuleRoot();
		MavenModuleSet project = (MavenModuleSet) mavenBuild.getProject();
		FilePath pom = null;

		if (moduleRoot.getName().endsWith(BUILD_FILE)) {
			pom = moduleRoot;
		} else {
			pom = new FilePath(moduleRoot, project.getRootPOM());
		}

		Document pomDocument = null;
		try {
			pomDocument = pom.act(new FileCallable<Document>() {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				public Document invoke(File pom, VirtualChannel channel)
						throws IOException, InterruptedException {

					try {
						DocumentBuilder documentBuilder = null;
						documentBuilder = DocumentBuilderFactory.newInstance()
								.newDocumentBuilder();
						return documentBuilder.parse(pom);

					} catch (SAXException e) {
						throw new InterruptedException(pom
								.getAbsolutePath()
								+ " is not a valid POM file.");
					} catch (ParserConfigurationException e) {
						throw new InterruptedException(pom
								.getAbsolutePath()
								+ " is not a valid POM file.");
					}
				}

			});
		} catch (InterruptedException e) {
			throw new InvalidBuildFileFormatException(e.getMessage());
		}

		return pomDocument;
	}

	@SuppressWarnings("unchecked")
	public Descriptor<BuildDefinitionParser> getDescriptor() {
		return new AbstractSematicParserDescription() {

			@Override
			public String getDisplayName() {

				return "Maven Pom Parserer";
			}
		};
	}
}
