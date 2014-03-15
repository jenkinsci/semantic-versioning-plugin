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

package org.jenkinsci.plugins.SemanticVersioning.test;

import org.jenkinsci.plugins.SemanticVersioning.AppVersion;
import org.junit.Test;
import org.jvnet.hudson.test.WithoutJenkins;

import static org.junit.Assert.*;

public class AppVersionTests {

    @Test
    @WithoutJenkins
    public void testNonSnapshotVersionWithNoBuildNumber() {
        System.out.println("####> testNonSnapshotVersionWithNoBuildNumber");
        final String versionString = "1.2";
        AppVersion version = AppVersion.parse(versionString);

        assertEquals(1, version.getMajor());
        assertEquals(2, version.getMinor());
        assertEquals(-1, version.getBuild());
        assertFalse(version.isSnapshot());
        assertEquals(versionString, version.toString());
        assertEquals(versionString, version.getOriginal());
    }

    @Test
    @WithoutJenkins
    public void testNonSnapshotVersionWithModifiedBuildNumber() {
        System.out.println("####> testNonSnapshotVersionWithModifiedBuildNumber");
        final String versionString = "1.2";
        final int BUILD = 47;
        AppVersion version = AppVersion.parse(versionString);
        version.setBuild(BUILD);

        assertEquals(1, version.getMajor());
        assertEquals(2, version.getMinor());
        assertEquals(47, version.getBuild());
        assertFalse(version.isSnapshot());
        assertEquals(versionString + "." + BUILD, version.toString());
        assertEquals(versionString, version.getOriginal());
    }

    @Test
    @WithoutJenkins
    public void testSnapshotVersionWithNoBuildNumber() {
        System.out.println("####> testSnapshotVersionWithNoBuildNumber");
        final String versionString = "1.2-SNAPSHOT";
        AppVersion version = AppVersion.parse(versionString);

        assertEquals(1, version.getMajor());
        assertEquals(2, version.getMinor());
        assertEquals(-1, version.getBuild());
        assertTrue(version.isSnapshot());
        assertEquals(versionString, version.toString());
        assertEquals(versionString, version.getOriginal());
    }

    @Test
    @WithoutJenkins
    public void testNonSnapshotVersionWithBuildNumber() {
        System.out.println("####> testNonSnapshotVersionWithBuildNumber");
        final String versionString = "1.2.3";
        AppVersion version = AppVersion.parse(versionString);

        assertEquals(1, version.getMajor());
        assertEquals(2, version.getMinor());
        assertEquals(3, version.getBuild());
        assertFalse(version.isSnapshot());
        assertEquals(versionString, version.toString());
        assertEquals(versionString, version.getOriginal());
    }

    @Test
    @WithoutJenkins
    public void testSnapshotVersionWithBuildNumber() {
        System.out.println("####> testSnapshotVersionWithBuildNumber");
        final String versionString = "1.2.3-SNAPSHOT";
        AppVersion version = AppVersion.parse(versionString);

        assertEquals(1, version.getMajor());
        assertEquals(2, version.getMinor());
        assertEquals(3, version.getBuild());
        assertTrue(version.isSnapshot());
        assertEquals(versionString, version.toString());
        assertEquals(versionString, version.getOriginal());
    }
}
