package org.jenkinsci.plugins.SemanticVersioning;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Builder;
import net.sf.json.JSONObject;
import org.jenkinsci.plugins.SemanticVersioning.parsing.BuildDefinitionParser;
import org.jenkinsci.plugins.SemanticVersioning.parsing.PomParser;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;

public class SemanticVersioningBuilder extends Builder {

    private BuildDefinitionParser parser;
    private boolean useJenkinsBuildNumber;

    @DataBoundConstructor
    public SemanticVersioningBuilder(String parser, boolean useJenkinsBuildNumber) {
//        try {
            this.parser = new PomParser();
//            this.parser = (BuildDefinitionParser) Jenkins.getInstance().getExtensionList(parser).iterator().next();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
        this.useJenkinsBuildNumber = useJenkinsBuildNumber;
    }

//    @Extension
//    public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();

    public BuildStepMonitor getRequiredMonitorService() { return BuildStepMonitor.NONE; }

    @Override
    public boolean perform(AbstractBuild<?,?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        System.out.println(">>>>>>>>>>>>>>>>> SemanticVersioningBuilder::perform");
        SemanticVersioningApp semanticVersioningApp = new SemanticVersioningApp(
                build,
                this.parser,
                this.useJenkinsBuildNumber,
                ".semver");
        semanticVersioningApp.determineSemanticVersion();
        return true;
    }
//
//    @Override
//    public DescriptorImpl getDescriptor() {
//        return DESCRIPTOR;
//    }

    @Extension(ordinal=9999)
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {
        public DescriptorImpl() {
            super(SemanticVersioningBuilder.class);
            load();
        }

        @Override
        public String getDisplayName() {
            return "Determine Semantic Version (Builder)";
        }

        @Override
        public boolean isApplicable(Class clazz) {
            return true;
        }

        @Override
        public Builder newInstance(StaplerRequest req, JSONObject formData) throws FormException {
            return super.newInstance(req, formData);
        }
    }
}
