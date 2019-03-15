package gs.nick


import gs.nick.server.definitions.WireGame
import slick.jdbc.MySQLProfile
import scala.concurrent.Future
import slick.jdbc.MySQLProfile.api._
import scala.concurrent.ExecutionContext.Implicits.global

// scala slick
// read up at http://slick.lightbend.com/doc/3.2.0/queries.html

case class DbGame(
  id: Int,
  name: String,
  sysId: Int,
  release: Option[String],
  comment: String,
  hasCase: Int,
  hasDocs: Int,
  isGHit: Int,
  isLimited: Int,
  isComplete: Int,
  isBroken: Int
) {
  def toWire: WireGame = {
    WireGame(
      Option(id), name, sysId, release, Option(hasCase), Option(hasDocs),
      Option(isGHit), Option(isLimited), Option(isComplete), Option(isBroken), comment
    )
  }
}

object DbGame {

  def fromWire(wire: WireGame): DbGame = {
    DbGame(
      wire.id.getOrElse(-1),
      wire.name,
      wire.sysid,
      wire.release,
      wire.comment,
      wire.hasCase.getOrElse(0),
      wire.hasDocs.getOrElse(0),
      wire.isGhit.getOrElse(0),
      wire.isLimited.getOrElse(0),
      wire.isComplete.getOrElse(0),
      wire.isBroken.getOrElse(0)
    )
  }

}

class GamesTable(tag: Tag) extends Table[DbGame](tag, "games") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def sysid = column[Int]("sysid")
  def release = column[Option[String]]("release")
  def comment = column[String]("comment")
  def hasCase = column[Int]("has_case")
  def hasDocs = column[Int]("has_docs")
  def isGHit = column[Int]("is_ghit")
  def isLimited = column[Int]("is_limited")
  def isComplete = column[Int]("is_complete")
  def isBroken = column[Int]("is_broken")

  def * = (id, name, sysid, release, comment, hasCase, hasDocs, isGHit, isLimited, isComplete, isBroken) <> ((DbGame.apply _).tupled, DbGame.unapply _)
}

object GamesTable {
  val allGames = TableQuery[GamesTable]
}

class GamesDao(db: MySQLProfile.backend.DatabaseDef) extends GamesDaoTrait {

  def getAllGames: Future[Seq[DbGame]] = {
    db.run(GamesTable.allGames.result)
  }

  def getAllBySystem(systemId: Int): Future[Seq[DbGame]] = {
    db.run(GamesTable.allGames.filter(_.sysid === systemId).result)
  }

  def getGame(id: Int): Future[Option[DbGame]] = {
    db.run(GamesTable.allGames.filter(_.id === id).result).map(_.headOption)
  }

  /**
    * Insert a game. On success, return the new ID of the game
    *
    * @param game
    * @return
    */
  def addGame(game: DbGame): Future[Int] = {
    val data = GamesTable.allGames
    val insertAction = (data returning data.map(_.id)) += game
    db.run(insertAction)
  }

}
