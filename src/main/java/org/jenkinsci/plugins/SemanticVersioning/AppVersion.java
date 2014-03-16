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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AppVersion {
    private static final String SNAPSHOT = "SNAPSHOT";
    private final int major;
    private final int minor;
    private final String original;

    private int build;
    private final boolean isSnapshot;
    public static final AppVersion EmptyVersion = new AppVersion(0, 0, 0, false, "");

    private static Logger logger = LogManager.getLogger(AppVersion.class);

    public static AppVersion parse(String versionString) {
        logger.debug("given versionString(" + versionString + ")");
        String[] parts = versionString.split("[\\.-]");

        int major = Integer.parseInt(parts[0]);
        int minor = Integer.parseInt(parts[1]);

        int build = -1;
        boolean isSnapshot = false;

        if (parts.length == 3) {
            if (parts[2].equals(SNAPSHOT)) {
                isSnapshot = true;
            } else {
                build = Integer.parseInt(parts[2]);
            }
        } else if (parts.length == 4) {
            build = Integer.parseInt(parts[2]);
            isSnapshot = parts[3].equals(SNAPSHOT);
        }

        return new AppVersion(major, minor, build, isSnapshot, versionString);
    }

    private AppVersion(int major, int minor, int build, boolean isSnapshot, String original) {
        this.major = major;
        this.minor = minor;

        this.isSnapshot = isSnapshot;
        this.build = build;
        this.original = original;
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public String getOriginal() {
        return this.original;
    }

    public boolean isSnapshot() {
        return isSnapshot;
    }

    public void setBuild(int build) {
        this.build = build;
    }

    public int getBuild() {
        return build;
    }

    @Override
    public String toString() {
        String version = String.format("%s.%s", major, minor);
        if(build > -1) {
            version += "." + build;
        }
        if (isSnapshot) {
            version += "-" + SNAPSHOT;
        }
        return version;
    }
}
