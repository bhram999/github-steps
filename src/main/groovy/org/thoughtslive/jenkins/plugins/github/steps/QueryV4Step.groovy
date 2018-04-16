package org.thoughtslive.jenkins.plugins.github.steps

import static org.thoughtslive.jenkins.plugins.github.util.Common.buildErrorResponse

import hudson.Extension
import hudson.Util
import org.jenkinsci.plugins.workflow.steps.StepContext
import org.jenkinsci.plugins.workflow.steps.StepExecution
import org.kohsuke.stapler.DataBoundConstructor
import org.thoughtslive.jenkins.plugins.github.api.ResponseData
import org.thoughtslive.jenkins.plugins.github.util.GithubStepDescriptorImpl
import org.thoughtslive.jenkins.plugins.github.util.GithubStepExecution
import org.thoughtslive.jenkins.plugins.github.service.GithubService

/**
 * Step to query Github V4.
 *
 */
class QueryV4Step extends BasicGithubStep {

    private static final long serialVersionUID = 387862257528432812L

    private final Object query

    @DataBoundConstructor
    QueryV4Step(final Object query) {
        this.query = query
    }

    @Override
    StepExecution start(StepContext context) throws Exception {
        return new Execution(this, context)
    }

    @Extension
    static class DescriptorImpl extends GithubStepDescriptorImpl {

        @Override
        String getFunctionName() {
            return "QueryV4"
        }

        @Override
        String getDisplayName() {
            return getPrefix() + "Query V4"
        }

    }

    static class Execution extends GithubStepExecution<ResponseData<Object>> {

        private static final long serialVersionUID = 211769231724671924L

        private final QueryV4Step step

        protected Execution(final QueryV4Step step, final StepContext context) throws IOException, InterruptedException {
            super(context)
            this.step = step
        }

        @Override
        protected ResponseData<Object> run() throws Exception {

            ResponseData<Object> response = verifyInput()

            if (response == null) {
                logger.println("Github: Site - " + siteName + " - Querying V4 with query: " + step.query())
                GithubService gitservice = new GithubService()
                response = gitservice.queryV4(step.query)
            }

            return logResponse(response)
        }

        @Override
        protected <T> ResponseData<T> verifyInput() throws Exception {
            String errorMessage = null
            ResponseData<T> response = verifyCommon(step)

            if (response == null) {
                if (Util.fixEmpty(step.query()) == null) {
                    errorMessage = "query is empty or null."
                }

                if (errorMessage != null) {
                    response = buildErrorResponse(new RuntimeException(errorMessage))
                }
            }
            return response
        }
    }
}
