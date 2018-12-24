package gs.nick

import akka.actor.ActorSystem
import gs.nick.server.AkkaHttpImplicits._
import gs.nick.server.games.GamesResource
import akka.http.scaladsl.server.HttpApp
import akka.http.scaladsl.server.Route
import akka.stream.{ActorMaterializer, ActorMaterializerSettings, Supervision}

import scala.concurrent.ExecutionContext

// Server definition
// see https://doc.akka.io/docs/akka-http/current/routing-dsl/HttpApp.html
class WebServer extends HttpApp {

  implicit val restActorSystem: ActorSystem = ActorSystem(name="todos-api")
  implicit val executionContext: ExecutionContext = restActorSystem.dispatcher
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  def getPort: Int = {
    val sPort = sys.env.getOrElse("PORT", "8080")
    sPort.toInt
  }

  val gamesController = new GamesController

  override def routes: Route = {

    val homeRoutes = pathSingleSlash { get { complete("The server is running :-D ")}}

    homeRoutes ~ GamesResource.routes(gamesController)
  }
}

object App {
  def main(args: Array[String]) = {
    val server = new WebServer
  	val port = server.getPort
    systemDebug()
    println(s"STARTUP port = $port")
    server.startServer("0.0.0.0", port)
    println(s"SHUTDOWN server has exited")
  }

  def systemDebug(): Unit = {
    Seq(
      "----------",
      "System Info:",
      "java.version = " + System.getProperty("java.version"),
      "java.vm.name = " + System.getProperty("java.vm.name"),
      "java.vendor = " + System.getProperty("java.vendor"),
      "java.class.path = " + System.getProperty("java.class.path"),
      "os.name = " + System.getProperty("os.name"),
      "os.arch = " + System.getProperty("os.arch"),
      "os.version = " + System.getProperty("os.version"),
      "----------"
    ).foreach(println)
  }
}
