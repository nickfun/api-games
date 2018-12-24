package gs.nick.tests

import gs.nick.WebServer
import akka.http.scaladsl.testkit.ScalatestRouteTest
import gs.nick.server.AkkaHttpImplicits._
import org.scalatest.FunSpec


// Testing an Akka HTTP app notes at https://doc.akka.io/docs/akka-http/current/routing-dsl/testkit.html#table-of-inspectors
class AppTestSuite extends FunSpec with ScalatestRouteTest {


  def generateServer: WebServer = {
    new gs.nick.WebServer
  }

  describe("Basic routes") {
    val routes = generateServer.routes

    it("responds to root when running") {
      Get("/") ~> routes ~> check {
        assert(status.intValue === 200)
        assert(true === responseAs[String].contains("server is running"))
      }
    }
  }

}
