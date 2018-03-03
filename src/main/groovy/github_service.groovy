import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response

class GithubService {

    private String authToken

    public GithubService(String authToken) {
        this.authToken = authToken
    }

    OkHttpClient client = new OkHttpClient()

    public post(json) {
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json)
        Request request = new Request.Builder()
                .header("Authorization", "bearer $authToken")
                .url('https://api.github.com/graphql')
                .post(body)
                .build()
        Response response = client.newCall(request).execute()
        return response.body().string()
    }
}

