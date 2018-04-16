package org.thoughtslive.jenkins.plugins.github.steps

import org.jenkinsci.plugins.workflow.steps.Step
import org.kohsuke.stapler.DataBoundSetter

/**
 * Base class for all Github steps
 */
abstract class BasicGithubStep extends Step implements Serializable {

    private static final long serialVersionUID = 7268920801605705697L

    @DataBoundSetter
    String site

    @DataBoundSetter
    boolean failOnError = true

    @DataBoundSetter
    boolean auditLog = true
}
