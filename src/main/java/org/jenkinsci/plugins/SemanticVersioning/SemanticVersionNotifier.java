package org.jenkinsci.plugins.SemanticVersioning;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.tasks.*;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

public class SemanticVersionNotifier extends Notifier {
    public BuildStepMonitor getRequiredMonitorService() { return BuildStepMonitor.STEP; }

    @DataBoundConstructor
    public SemanticVersionNotifier() {
    }

    @Override
    public boolean needsToRunAfterFinalized() { return true; }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) {
//        System.out.println("build = [" + build + "], launcher = [" + launcher + "], listener = [" + listener + "]");
        System.out.println("$$$[[[[[[[[[[   SemanticVersionNotifier::perform  ]]]]]]]]]]]]$$$");
        return true;
    }

    @Extension(ordinal=9999)
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {

        public DescriptorImpl() {
            super(SemanticVersionNotifier.class);
        }

        @Override
        public String getDisplayName() {
            return "Determine Semantic Version (Notifier)";
        }

        @Override
        public boolean isApplicable(Class clazz) {
            return true;
        }

        @Override
        public Publisher newInstance(StaplerRequest req, JSONObject formData) throws FormException {
            return super.newInstance(req, formData);
        }
    }
}
