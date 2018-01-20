package org.thoughtslive.jenkins.plugins.github.service

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface GithubV4EndPoints {

    @POST("graphql")
    Call<ResponseBody> queryV4(@Body Object body)

}
