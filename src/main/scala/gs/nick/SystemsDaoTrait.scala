package gs.nick

import scala.concurrent.Future

trait SystemsDaoTrait {

  def getAllSystems: Future[Seq[DbSystem]]

  def getSystemById(id: Int): Future[Option[DbSystem]]

  def addSystem(newSystem: DbSystem): Future[Int]
}
