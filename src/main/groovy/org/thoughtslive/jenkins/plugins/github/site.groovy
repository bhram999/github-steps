package org.thoughtslive.jenkins.plugins.github

import hudson.Extension
import hudson.Util
import hudson.model.AbstractDescribableImpl
import hudson.model.Descriptor
import hudson.util.FormValidation
import hudson.util.Secret
import groovy.util.logging.Log
import org.kohsuke.stapler.DataBoundConstructor
import org.kohsuke.stapler.DataBoundSetter
import org.kohsuke.stapler.QueryParameter
import org.thoughtslive.jenkins.plugins.github.service.GithubService

/**
 * Represents a configuration needed to access this Github.
 */
@Log
class Site extends AbstractDescribableImpl<Site> {

    String name
    final URL url
    final String loginType
    int timeout
    int readTimeout
    // Basic
    String userName
    Secret password
    // Token
    Secret token
    private transient GithubService GithubService = null

    @DataBoundConstructor
    Site(final String name, final URL url, final String loginType, final int timeout) {
        this.name = Util.fixEmpty(name)
        this.url = url
        this.loginType = Util.fixEmpty(loginType)
        this.timeout = timeout
    }

    static Site get(final String siteName) {
        Site[] sites = Config.DESCRIPTOR.getSites()
        for (Site site : sites) {
            if (site.getName().equalsIgnoreCase(siteName)) {
                return site
            }
        }
        return null
    }

    String isLoginType(String loginType) {
        return this.loginType.equalsIgnoreCase(loginType) ? "true" : ""
    }

    @DataBoundSetter
    void setPassword(final String password) {
        this.password = Secret.fromString(Util.fixEmpty(password))
    }

    @DataBoundSetter
    void setReadTimeout(final int readTimeout) {
        this.readTimeout = readTimeout
    }

    @DataBoundSetter
    void setToken(final String token) {
        this.token = Secret.fromString(Util.fixEmpty(token))
    }

    GithubService getService() {
        if (GithubService == null) {
            this.GithubService = new GithubService(this)
        }
        return GithubService
    }

    enum LoginType {
        BASIC, TOKEN
    }

    @Extension
    static class DescriptorImpl extends Descriptor<Site> {

        @Override
        String getDisplayName() {
            return "Github Steps: Site"
        }

        /**
         * Checks if the details required for the basic org.thoughtslive.jenkins.plugins.github.login is valid. TODO: This validation can be
         * moved to Config so that we can also verify the name is valid.
         */
        FormValidation doValidateBasic(@QueryParameter String name, @QueryParameter String url,
                                       @QueryParameter String loginType, @QueryParameter String timeout,
                                       @QueryParameter String readTimeout,
                                       @QueryParameter String userName, @QueryParameter String password,
                                       @QueryParameter String token) throws IOException {
            url = Util.fixEmpty(url)
            name = Util.fixEmpty(name)
            userName = Util.fixEmpty(userName)
            password = Util.fixEmpty(password)
            URL mainURL = null

            if (name == null) {
                return FormValidation.error("Name is empty or null.")
            }

            try {
                if (url == null) {
                    return FormValidation.error("No URL given.")
                }
                mainURL = new URL(url)
            } catch (MalformedURLException e) {
                return FormValidation.error(String.format("Malformed URL (%s)", url), e)
            }

            int t = 0
            try {
                t = Integer.parseInt(timeout)
                if (t <= 100) {
                    return FormValidation.error("Timeout can't be less than 100.")
                }
            } catch (NumberFormatException e) {
                return FormValidation.error("Timeout is not a number")
            }

            int rt = 0
            try {
                rt = Integer.parseInt(readTimeout)
                if (rt <= 100) {
                    return FormValidation.error("Read Timeout can't be less than 100.")
                }
            } catch (NumberFormatException e) {
                return FormValidation.error("Read Timeout is not a number")
            }

            Site site = new Site(name, mainURL, "BASIC", t)

            if (userName == null) {
                return FormValidation.error("UserName is empty or null.")
            }
            if (password == null) {
                return FormValidation.error("Password is empty or null.")
            }
            site.setUserName(userName)
            site.setPassword(password)
            site.setReadTimeout(rt)

            try {
                final GithubService service = new GithubService(site)
                final response = service.query(["query":"{ __typename }"])
                if (response.isSuccessful()) {
                    return FormValidation.ok("Success: " + response.getMessage().toString())
                } else {
                    return FormValidation.error("Code: " + response.getCode().toString() + " Error: " + response.getError().toString())
                }
            } catch (Exception e) {
                log.warning "Failed to Basic org.thoughtslive.jenkins.plugins.github.login to Github at " + url + e
            }
            return FormValidation.error("Failed to Basic org.thoughtslive.jenkins.plugins.github.login to Github: " + url)
        }

        // This is stupid but no choice as I couldn't find the way to get the
        // value loginType (radioBlock as a @QueryParameter)
        FormValidation doValidateToken(@QueryParameter String name, @QueryParameter String url,
                                       @QueryParameter String loginType, @QueryParameter String timeout,
                                       @QueryParameter String readTimeout,
                                       @QueryParameter String userName, @QueryParameter String password,
                                       @QueryParameter String token) throws IOException {
            url = Util.fixEmpty(url)
            name = Util.fixEmpty(name)
            token = Util.fixEmpty(token)
            URL mainURL = null

            if (name == null) {
                return FormValidation.error("Name is empty.")
            }

            try {
                if (url == null) {
                    return FormValidation.error("No URL given.")
                }
                mainURL = new URL(url)
            } catch (MalformedURLException e) {
                return FormValidation.error(String.format("Malformed URL (%s)", url), e)
            }

            int t = 0
            try {
                t = Integer.parseInt(timeout)
                if (t <= 100) {
                    return FormValidation.error("Timeout can't be less than 100.")
                }
            } catch (NumberFormatException e) {
                return FormValidation.error("Timeout is not a number")
            }

            int rt = 0
            try {
                rt = Integer.parseInt(readTimeout)
                if (rt <= 100) {
                    return FormValidation.error("Read Timeout can't be less than 100.")
                }
            } catch (NumberFormatException e) {
                return FormValidation.error("Read Timeout is not a number")
            }

            Site site = new Site(name, mainURL, "TOKEN", t)

            if (token == null) {
                return FormValidation.error("Token is empty or null.")
            }
            site.setToken(token)
            site.setReadTimeout(rt)

            try {
                final GithubService service = new GithubService(site)
                final response = service.query(["query":"{ __typename }"])
//                final response = service.query({ __typename })
                if (response.isSuccessful()) {
                    return FormValidation.ok("Success: " + response.getMessage().toString())
                } else {
                    return FormValidation.error("Code: " + response.getCode().toString() + "Error: " + response.getError().toString())
                }
            } catch (Exception e) {
                log.warning "Failed to Token org.thoughtslive.jenkins.plugins.github.login to Github at " + url + e
            }
            return FormValidation.error("Failed to Token org.thoughtslive.jenkins.plugins.github.login to Github: " + url)
        }
    }
}
