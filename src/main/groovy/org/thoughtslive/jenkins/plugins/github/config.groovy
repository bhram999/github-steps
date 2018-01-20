package org.thoughtslive.jenkins.plugins.github

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings
import hudson.Extension
import hudson.model.AbstractDescribableImpl
import hudson.model.Descriptor
import hudson.model.Descriptor.FormException
import hudson.util.CopyOnWriteList
import net.sf.json.JSONObject
import org.apache.commons.beanutils.Converter
import org.kohsuke.accmod.Restricted
import org.kohsuke.accmod.restrictions.NoExternalUse
import org.kohsuke.stapler.DataBoundConstructor
import org.kohsuke.stapler.Stapler
import org.kohsuke.stapler.StaplerRequest

import javax.annotation.Nonnull

/**
 * Represents Github Global Configuration.
 **/
@SuppressFBWarnings
 class Config extends AbstractDescribableImpl<Config> {

    @Extension
    public static final ConfigDescriptorImpl DESCRIPTOR = new ConfigDescriptorImpl()
    public final String siteName

    @DataBoundConstructor
     Config(String siteName) {
        if (siteName == null) {
            Site[] sites = DESCRIPTOR.getSites()
            if (sites.length > 0) {
                siteName = sites[0].getName()
            }
        }
        this.siteName = siteName
    }

     Site getSite() {
        Site[] sites = DESCRIPTOR.getSites()
        if (siteName == null && sites.length > 0) {
            // default
            return sites[0]
        }

        for (Site site : sites) {
            if (site.getName().equals(siteName)) {
                return site
            }
        }
        return null
    }

    @Override
    ConfigDescriptorImpl getDescriptor() {
        return DESCRIPTOR
    }

    static final class ConfigDescriptorImpl extends Descriptor<Config>
            implements Serializable {

        private static final long serialVersionUID = 6174559183832237318L
        private final CopyOnWriteList<Site> sites = new CopyOnWriteList<Site>()

        ConfigDescriptorImpl() {
            super(Config.class)
            load()
        }

        @Override
        String getDisplayName() {
            return "Github Steps: Config"
        }

        Site[] getSites() {
            return sites.toArray(new Site[0])
        }

        void setSites(Site site) {
            sites.add(site)
        }

        @Override
        Config newInstance(@Nonnull final StaplerRequest req, final JSONObject formData) throws FormException {
            Config githubConfig = req.bindJSON(Config.class, formData)
            if (githubConfig.siteName == null) {
                githubConfig = null
            }
            return githubConfig
        }

        @Override
        boolean configure(StaplerRequest req, JSONObject formData) {
            Stapler.CONVERT_UTILS.deregister(java.net.URL.class)
            Stapler.CONVERT_UTILS.register(new EmptyFriendlyURLConverter(), java.net.URL.class)
            sites.replaceBy(req.bindJSONToList(Site.class, formData.get("sites")))
            save()
            return true
        }

        @Restricted(NoExternalUse.class)
        static class EmptyFriendlyURLConverter implements Converter {

            @Override
            Object convert(@SuppressWarnings("rawtypes") Class aClass, Object o) {
                if (o == null || "".equals(o) || "null".equals(o)) {
                    return null
                }
                try {
                    return new URL(o.toString())
                } catch (MalformedURLException e) {
                    return null
                }
            }
        }
    }
}
