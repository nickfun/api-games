package gs.nick
import scala.concurrent.Future

trait GamesDaoTrait {

  def getAllGames: Future[Seq[DbGame]]

  def getAllBySystem(systemId: Int): Future[Seq[DbGame]]

  def getGame(id: Int): Future[Option[DbGame]]

  def addGame(game: DbGame): Future[Int]
}
