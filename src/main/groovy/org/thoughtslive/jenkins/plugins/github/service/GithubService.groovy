package org.thoughtslive.jenkins.plugins.github.service

import static org.thoughtslive.jenkins.plugins.github.util.Common.buildErrorResponse
import static org.thoughtslive.jenkins.plugins.github.util.Common.parseResponse

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.joda.JodaModule
import java.util.concurrent.TimeUnit
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import org.thoughtslive.jenkins.plugins.github.api.ResponseData
import org.thoughtslive.jenkins.plugins.github.Site
import org.thoughtslive.jenkins.plugins.github.login.SigningInterceptor

import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.jackson.JacksonConverterFactory

class GithubService {

    private final Site gitSite
    private final GithubV4EndPoints githubV4EndPoints

    GithubService(final Site gitSite) {
        this.gitSite = gitSite
        final ConnectionPool CONNECTION_POOL = new ConnectionPool(5, 60, TimeUnit.SECONDS)

        OkHttpClient httpClient = new OkHttpClient.Builder()
                .connectTimeout(gitSite.getTimeout(), TimeUnit.MILLISECONDS)
                .readTimeout(gitSite.getReadTimeout(), TimeUnit.MILLISECONDS)
                .connectionPool(CONNECTION_POOL)
                .retryOnConnectionFailure(true).addInterceptor(new SigningInterceptor(gitSite)).build()

        final ObjectMapper mapper = new ObjectMapper()
        mapper.registerModule(new JodaModule())
        this.githubV4EndPoints = new Retrofit.Builder().baseUrl(this.gitSite.getUrl().toString())
                .addConverterFactory(JacksonConverterFactory.create(mapper))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(httpClient).build()
                .create(GithubV4EndPoints.class)
    }

    /**
     * @return github graphql response.
     */
    ResponseData<Object> queryV4(json) {
        try {
            return parseResponse(githubV4EndPoints.queryV4(json).execute())
        } catch (Exception e) {
            return buildErrorResponse(e)
        }
    }
}
