package org.jenkinsci.plugins.SemanticVersioning;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Builder;
import hudson.util.ListBoxModel;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.jenkinsci.plugins.SemanticVersioning.parsing.BuildDefinitionParser;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;

public class SemanticVersioningBuilder extends Builder {

    private BuildDefinitionParser parser;
    private boolean useJenkinsBuildNumber;

    @DataBoundConstructor
    public SemanticVersioningBuilder(String parser, boolean useJenkinsBuildNumber) {
        this.useJenkinsBuildNumber = useJenkinsBuildNumber;
        try {
            this.parser = (BuildDefinitionParser) Jenkins.getInstance().getExtensionList(parser).iterator().next();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println("##### SemanticVersioningBuilder::ctor parser = [" + parser + "], useJenkinsBuildNumber = [" + useJenkinsBuildNumber + "]");
    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    public boolean getUseJenkinsBuildNumber() {
        return this.useJenkinsBuildNumber;
    }

    /**
     * Used from <tt>config.jelly</tt>.
     *
     * @return the canonical class name of the parser  which the semantic version use
     * to parse version number
     */
    public String getParser() {
        return this.parser.getClass().getCanonicalName();
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        System.out.println("##### SemanticVersioningBuilder::perform");
        SemanticVersioningProcesser semanticVersioningApp = new SemanticVersioningProcesser(
                build,
                this.parser,
                this.useJenkinsBuildNumber,
                Messages.SEMANTIC_VERSION_FILENAME);
        semanticVersioningApp.determineSemanticVersion();
        return true;
    }

    @Extension
    public static final DescriptorImpl descriptor = new DescriptorImpl();

    @Extension(ordinal = 9999)
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {
        /**
         * In order to load the persisted global configuration, you have to call
         * load() in the constructor.
         */
        public DescriptorImpl() {
            super(SemanticVersioningBuilder.class);
            load();
        }

        /**
         * Generates ListBoxModel for available BuildDefinitionParsers
         *
         * @return available BuildDefinitionParsers as ListBoxModel
         */
        public ListBoxModel doFillParserItems() {
            ListBoxModel parsersModel = new ListBoxModel();
            for (BuildDefinitionParser parser : Jenkins.getInstance()
                    .getExtensionList(BuildDefinitionParser.class)) {
                parsersModel.add(parser.getDescriptor().getDisplayName(), parser
                        .getClass().getCanonicalName());
            }

            return parsersModel;
        }

        public boolean getDefaultUseJenkinsBuildNumber() {
            return true;
        }

        @Override
        public String getDisplayName() {
            return Messages.DISPLAY_NAME;
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
