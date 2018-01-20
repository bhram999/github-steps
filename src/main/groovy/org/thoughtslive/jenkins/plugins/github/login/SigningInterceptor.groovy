package org.thoughtslive.jenkins.plugins.github.login

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import org.thoughtslive.jenkins.plugins.github.Site

@SuppressFBWarnings
class SigningInterceptor implements Interceptor {

    private final Site gitSite

    SigningInterceptor(Site gitSite) {
        this.gitSite = gitSite
    }

    @Override
    Response intercept(Interceptor.Chain chain) throws IOException {

        if (gitSite.getLoginType().equalsIgnoreCase(Site.LoginType.BASIC.name())) {
            String credentials = gitSite.getUserName() + ":" + gitSite.getPassword().getPlainText()
            String encodedHeader = "Basic " + new String(Base64.getEncoder().encode(credentials.getBytes()))
            Request requestWithAuthorization = chain.request().newBuilder().addHeader("Authorization", encodedHeader).build()
            return chain.proceed(requestWithAuthorization)
        } else if (gitSite.getLoginType().equalsIgnoreCase(Site.LoginType.TOKEN.name())) {
            String token = "bearer " + gitSite.getToken()
            Request requestWithAuthorization = chain.request().newBuilder().addHeader("Authorization", token).build()
            return chain.proceed(requestWithAuthorization)
        } else {
            throw new IOException("Invalid Login Type, this isn't expected.")
        }
    }
}
