package gs.nick

import gs.nick.server.definitions.WireGame
import gs.nick.server.games.{GamesHandler, GamesResource}
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import collection.breakOut


class GamesController(gamesDao: GamesDaoTrait)(implicit val ec: ExecutionContext) extends GamesHandler {

  val mylist: IndexedSeq[WireGame] = IndexedSeq.empty

  override def addGame(respond: GamesResource.addGameResponse.type)(newGame: WireGame): Future[GamesResource.addGameResponse] = {
    println("add game")
    val intF: Future[Int] = gamesDao.addGame(DbGame.fromWire(newGame))
    val x = intF
      .flatMap { newId =>
        val game = gamesDao.getGame(newId)
        println(s"new ID is $newId")
        game
      }
    x.map {
      case Some(dbgame) => respond.OK(dbgame.toWire)
      case None => respond.BadRequest("Sorry I can not create that game")
    }
  }

  override def getGameById(respond: GamesResource.getGameByIdResponse.type)(gameId: BigDecimal): Future[GamesResource.getGameByIdResponse] = {
    println("get game by id " + gameId)
    gamesDao.getGame(gameId.toInt).map {
      case Some(game: DbGame) => respond.OK(game.toWire)
      case None => respond.NotFound
    }
  }

  override def deleteGameById(respond: GamesResource.deleteGameByIdResponse.type)(gameId: BigDecimal): Future[GamesResource.deleteGameByIdResponse] = {
    println("delete game by id")
    Future {
      respond.OK
    }
  }

  override def updateGameById(respond: GamesResource.updateGameByIdResponse.type)(gameId: BigDecimal, newGame: WireGame): Future[GamesResource.updateGameByIdResponse] = {
    println("update game by id")
    Future {
      val g = WireGame(None, "Metal Gear", 1, None, None, None, None, None, None, None, "cool game bro")
      respond.OK(g)
    }
  }

  override def getGameList(respond: GamesResource.getGameListResponse.type)(systemId: Option[Int]): Future[GamesResource.getGameListResponse] = {
    println("Get games list: " + systemId.map(x=>s"system $x").getOrElse("all games"))
    if (systemId.isDefined) {
      gamesDao.getAllBySystem(systemId.get) map { all =>
        val asWire = all.map(_.toWire)(breakOut): IndexedSeq[WireGame]
        respond.OK(asWire)
      }
    } else {
      gamesDao.getAllGames map { all =>
        val asWire = all.map(_.toWire)(breakOut): IndexedSeq[WireGame]
        respond.OK(asWire)
      }
    }
  }
}
