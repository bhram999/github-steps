import groovy.json.*
import org.thoughtslive.jenkins.plugins.github.Site
import org.thoughtslive.jenkins.plugins.github.service.GithubService

class GithubServiceTest extends GroovyTestCase {
    void testSomething() {
        Site site = new Site('github', "https://api.github.com/".toURL(), "TOKEN", 1000)
        GithubService g = new GithubService(site)
//        def query = JsonOutput.toJson(["query":"{ viewer { org.thoughtslive.jenkins.plugins.github.login }}"])
//       println g.post('{"query":"{ viewer { org.thoughtslive.jenkins.plugins.github.login }}"}')
//       println g.post('{"query":"{\\n  repository(name: \\"github-steps\\", owner: \\"bhram999\\") {\\n    ref(qualifiedName: \\"master\\") {\\n      target {\\n        ... on Commit {\\n          id\\n          history(first: 2) {\\n            pageInfo {\\n              hasNextPage\\n            }\\n            edges {\\n              node {\\n                messageHeadline\\n                oid\\n                message\\n                author {\\n                  name\\n                  email\\n                  date\\n                }\\n              }\\n            }\\n          }\\n        }\\n      }\\n    }\\n  }\\n}"}')
//       println g.post('{"query":"{ viewer { org.thoughtslive.jenkins.plugins.github.login }}"}')
        println g.queryV4('{"query":"query {viewer {login}}"}')
    }
}
