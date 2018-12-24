package gs.nick.tests

import gs.nick.{DbGame, GamesDao, GamesDaoTrait, WebServer}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import gs.nick.server.AkkaHttpImplicits._
import org.scalatest.FunSpec

import scala.concurrent.Future


// Testing an Akka HTTP app notes at https://doc.akka.io/docs/akka-http/current/routing-dsl/testkit.html#table-of-inspectors
class AppTestSuite extends FunSpec with ScalatestRouteTest {



  def generateServer: WebServer = {
    new gs.nick.WebServer(DummyGamesDao)
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

// mocks

object DummyGamesDao extends GamesDaoTrait {
  override def getAllGames: Future[Seq[DbGame]] = ???

  override def getAllBySystem(systemId: Int): Future[Seq[DbGame]] = ???

  override def getGame(id: Int): Future[Option[DbGame]] = ???

  override def addGame(game: DbGame): Future[Int] = ???
}
