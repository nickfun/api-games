package gs.nick

import akka.actor.ActorSystem
import gs.nick.server.AkkaHttpImplicits._
import gs.nick.server.games.GamesResource
import akka.http.scaladsl.server.HttpApp
import akka.http.scaladsl.server.Route
import akka.stream.{ActorMaterializer, ActorMaterializerSettings, Supervision}
import gs.nick.server.systems.SystemsResource
import slick.jdbc
import slick.jdbc.MySQLProfile
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.ExecutionContext

// Server definition
// see https://doc.akka.io/docs/akka-http/current/routing-dsl/HttpApp.html
class WebServer(gamesDao: GamesDaoTrait, systemsDao: SystemsDaoTrait) extends HttpApp {

  implicit val restActorSystem: ActorSystem = ActorSystem(name = "todos-api")
  implicit val executionContext: ExecutionContext = restActorSystem.dispatcher
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  def getPort: Int = {
    val sPort = sys.env.getOrElse("PORT", "8080")
    sPort.toInt
  }

  val gamesController = new GamesController(gamesDao)
  val systemsController = new SystemsController(systemsDao)

  override def routes: Route = {

    val homeRoutes = pathSingleSlash { get { complete("The server is running :-D ") } }

    homeRoutes ~
      GamesResource.routes(gamesController) ~
      SystemsResource.routes(systemsController)
  }
}

object App {

  def main(args: Array[String]) = {
    val db = database()
    val gamesDao = new GamesDao(db)
    val systemsDao = new SystemsDao(db)
    val server = new WebServer(gamesDao, systemsDao)
    val port = server.getPort
    systemDebug()
    println(s"STARTUP port = $port")
    server.startServer("0.0.0.0", port)
    println(s"SHUTDOWN server has exited")
  }

  def ERR(msg: String) = {
    throw new RuntimeException(s"Error! $msg")
  }

  def database(): jdbc.MySQLProfile.backend.DatabaseDef = {
    val url = sys.env.getOrElse("DB_URL", ERR("missing env DB_URL"))
    val user = sys.env.getOrElse("DB_USER", ERR("missing env DB_USER"))
    val pass = sys.env.getOrElse("DB_PASS", "")
    val db: MySQLProfile.backend.DatabaseDef = Database.forURL(url, user, pass)
    db
  }

  def systemDebug(): Unit = {

    val properties = Seq(
      "java.version",
      "java.vm.name",
      "java.vendor",
      "java.class.path",
      "os.name",
      "os.arch",
      "os.version"
    ).map { sys =>
      val prop = System.getProperty(sys)
      s"$sys\n  $prop"
    }

    val border = "==================="

    (border +: properties :+ border).foreach(println)
  }
}
