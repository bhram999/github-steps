import groovy.json.*
class GithubServiceTest extends GroovyTestCase {
    void testSomething() {
        GithubService g = new GithubService('9a3caff3e1f6e5ae90661e9a4741d474ca4f10be')
//        def query = JsonOutput.toJson(["query":"{ viewer { login }}"])
       println g.post('{"query":"{ viewer { login }}"}')

    }
}