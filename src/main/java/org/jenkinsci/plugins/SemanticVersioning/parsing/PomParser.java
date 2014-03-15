package org.jenkinsci.plugins.SemanticVersioning.parsing;

import org.jenkinsci.plugins.SemanticVersioning.AppVersion;
import org.jenkinsci.plugins.SemanticVersioning.InvalidBuildFileFormatException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class PomParser implements BuildDefinitionParser {

    public AppVersion extractAppVersion(String filename) throws IOException, InvalidBuildFileFormatException {
        File file = new File(filename);
        if (file.exists()) {

            String version = null;
            DocumentBuilder documentBuilder = null;
            try {
                documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                Document document = documentBuilder.parse(file);
                XPath xPath = XPathFactory.newInstance().newXPath();
                XPathExpression expression = xPath.compile("/project/version");
                version = expression.evaluate(document);
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
                throw new InvalidBuildFileFormatException(filename + " is not a valid POM file.");
            } catch (XPathExpressionException e) {
                e.printStackTrace();
            }

            if(version == null || version.length() == 0) {
                throw new InvalidBuildFileFormatException("No version information found in " + filename);
            }

            return AppVersion.parse(version);
        } else {
            throw new FileNotFoundException("'" + filename + "' was not found.");
        }
    }
}
