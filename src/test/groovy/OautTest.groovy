import groovy.json.*
class OautTest extends GroovyTestCase {
    void testSomething() {
        Oaut g = new Oaut('9a3caff3e1f6e5ae90661e9a4741d474ca4f10be')
//        def query = JsonOutput.toJson(["query":"{ viewer { login }}"])
        def query = '{"query":"{ viewer { login }}"}'
        g.search(query)
    }
}
