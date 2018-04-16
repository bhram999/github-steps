package org.thoughtslive.jenkins.plugins.github.util

import com.google.common.collect.ImmutableSet
import hudson.EnvVars
import hudson.model.Run
import hudson.model.TaskListener
import hudson.util.ListBoxModel
import org.jenkinsci.plugins.workflow.steps.StepDescriptor
import org.thoughtslive.jenkins.plugins.github.Config
import org.thoughtslive.jenkins.plugins.github.Site

/**
 * Default StepDescriptorImpl for all Github steps.
 */
abstract class GithubStepDescriptorImpl extends StepDescriptor {

    /**
     * Fills the site names to the list box.
     *
     * @return {@link ListBoxModel}
     */
    ListBoxModel doFillSiteItems() {
        ListBoxModel list = new ListBoxModel()
        list.add("Please select", "")
        for (Site site : Config.DESCRIPTOR.getSites()) {
            list.add(site.getName())
        }
        return list
    }

    protected String getPrefix() {
        return "Github Steps: "
    }

    @Override
    Set<? extends Class<?>> getRequiredContext() {
        return ImmutableSet.of(Run.class, TaskListener.class, EnvVars.class)
    }
}
