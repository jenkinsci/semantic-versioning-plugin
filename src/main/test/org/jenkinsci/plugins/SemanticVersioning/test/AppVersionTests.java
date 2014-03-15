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
    }
}
