package gs.nick

import scala.concurrent.Future

trait SystemsDaoTrait {

  def getAllSystems: Future[Seq[DbSystem]]
}
