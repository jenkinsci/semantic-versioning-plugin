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

    private final String filename;

    public PomParser(String filename) {

        this.filename = filename;
    }

    public AppVersion extractAppVersion() throws IOException, InvalidBuildFileFormatException {
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
