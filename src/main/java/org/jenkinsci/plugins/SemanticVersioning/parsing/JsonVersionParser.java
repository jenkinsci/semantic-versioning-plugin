/*
 * The MIT License
 *
 * Copyright (c) 2014, Arne M. Størksen
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

import org.apache.commons.io.FileUtils;
import org.jenkinsci.plugins.SemanticVersioning.AppVersion;
import org.jenkinsci.plugins.SemanticVersioning.InvalidBuildFileFormatException;

import hudson.FilePath;
import net.sf.json.JSONObject;

public abstract class JsonVersionParser extends AbstractBuildDefinitionParser {
    
    private final String filepath;
    private final String jsonpathVersion;
    
    protected JsonVersionParser(String filepath, String jsonpathVersion) {
        this.filepath = filepath;
        this.jsonpathVersion = jsonpathVersion;
    }

    public AppVersion extractAppVersion(FilePath workspace, PrintStream logger) throws IOException, InvalidBuildFileFormatException {
    	        
        File file = new File(workspace.getRemote()+"/"+filepath);

        logger.println("looking for json file: "+file.getAbsolutePath()+" ("+file.exists()+")");
        
        if(file.exists()) {

            String content = FileUtils.readFileToString(file);
            
            if(content == null || content.isEmpty()) {

            	throw new InvalidBuildFileFormatException("ERROR: '" + filepath + "' is not a valid file.");

            } else {

                JSONObject out = JSONObject.fromObject( content );
            	String version = null;
                logger.println("looking for json elements ... ");
            	for(String s : jsonpathVersion.split("\\.")) {
            		Object o = out.get(s);
            		logger.println(" - "+s+" = "+o);
            		version = o.toString();
            		if(o instanceof JSONObject) {
            			out = (JSONObject)o;
            		} else {
            			break;
            		}
            	}
        		logger.println("returning version: "+version);
            	return AppVersion.parse(version);
            }
        } else {
            logger.println("ERROR: file '" + file.getAbsolutePath() + "' does not exist!");
            throw new FileNotFoundException("'" + filepath + "' was not found.");
        }
	}
}
