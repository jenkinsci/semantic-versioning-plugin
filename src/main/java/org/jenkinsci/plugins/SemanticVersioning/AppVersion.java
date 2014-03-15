package org.jenkinsci.plugins.SemanticVersioning;

public class AppVersion {
    private static final String SNAPSHOT = "SNAPSHOT";
    private final int major;
    private final int minor;

    public void setBuild(int build) {
        this.build = build;
    }

    private int build;
    private final boolean isSnapshot;

    public static final AppVersion EmptyVersion = new AppVersion(0, 0, 0, false);

    public static AppVersion parse(String versionString) {
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

        return new AppVersion(major, minor, build, isSnapshot);
    }

    private AppVersion(int major, int minor, int build, boolean isSnapshot) {
        this.major = major;
        this.minor = minor;

        this.isSnapshot = isSnapshot;
        this.build = build;
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public boolean isSnapshot() {
        return isSnapshot;
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
