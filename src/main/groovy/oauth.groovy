import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*

class Oaut {

    private String authToken

    public Oaut(String authToken) {
        this.authToken = authToken
    }

    public void search(query) {
        def http = new HTTPBuilder('https://api.github.com/graphql')

        http.request(POST, JSON) { req ->
            headers.'Authorization' = "bearer $authToken"
            headers.'Accept' = 'application/vnd.github.v4.idl'
            headers.'User-Agent' = 'Github-steps-plugin'
            body = query
            response.success = { resp, json ->
                println "Got response: ${resp.statusLine}"
                println "Content-Type: ${resp.headers.'Content-Type'}"
                println json
            }
            response.failure = { resp, json ->
                print json
            }
        }
    }
}
