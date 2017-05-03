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

public class Messages {
    public static final String SEMANTIC_VERSION_FILENAME = ".semanticVersion";
    public static final String DISPLAY_NAME = "Determine Semantic Version";
    public static final String SEMANTIC_VERSION_COLUMN_DISPLAY_NAME = "Semantic Version";
    public static final String UNKNOWN_VERSION = "Unknown";

    public class Parsers {
        public static final String SBT_BUILD_SBT_PARSER = "SBT build.sbt parser";
        public static final String MAVEN_POM_PARSER = "Maven Pom Parser";
        public static final String SBT_BUILD_SCALA_PARSER = "SBT Build.scala Parser";
        public static final String BOWER_JSON_PARSER = "Bower bower.json Parser";
        public static final String NPM_JSON_PARSER = "NPM package.json Parser";
        public static final String COMPOSER_JSON_PARSER = "Composer composer.json Parser";
    }

    public class ColumnDisplayStrategies {
        public static final String LAST_JENKINS_BUILD_NUMBER_STRATEGY = "Show Last Jenkins Build Number";
        public static final String LAST_SUCCESSFUL_JENKINS_BUILD_NUMBER_STRATEGY = "Show Last Successful Jenkins Build Number";
        public static final String LAST_SUCCESSFUL_BUILD_VERSION_STRATEGY = "Show Last Successful Semantic Version";
        public static final String NA_STRATEGY = "Show 'N/A'";
    }

    public class NamingStrategies {
    	public static final String NOOP_NAMING = "none";
    	public static final String MAVEN_RELEASE_DEVELOPMENT = "Maven, release and development version";
    	public static final String UNTOUCHED = "As found";
    	public static final String UNTOUCHED_SNAPSHOT = "As found (-SNAPSHOT)";
    }
}
