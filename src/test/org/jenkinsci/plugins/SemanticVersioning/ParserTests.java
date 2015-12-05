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

package org.jenkinsci.plugins.SemanticVersioning;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jenkinsci.plugins.SemanticVersioning.parsing.BuildDefinitionParser;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

public abstract class ParserTests {

    protected final String version = "1.2.3-SNAPSHOT";
    protected final Logger logger = LogManager.getLogger(ParserTests.class);

    protected abstract BuildDefinitionParser getParser(String filename);
    protected abstract void generateInvalidBuildFile(String filename) throws IOException;
    protected abstract void generateValidBuildFile(String filename) throws IOException;
    protected abstract void generateBuildFileWithMissingVersion(String filename) throws IOException;
    protected abstract String getExpectedInvalidBuildFileFormatExceptionMessage(String filename);

    protected void writeLinesToFile(String filename, Collection<String> lines) throws IOException {
        File file = new File(filename);
        if (file.exists()) {
            file.delete();
        }
        file.setWritable(true);

        FileUtils.writeLines(file, lines);
    }
}
