package gs.nick

import gs.nick.server.definitions.WireSystem
import gs.nick.server.systems.{SystemsHandler, SystemsResource}

import collection.breakOut
import scala.concurrent.{ExecutionContext, Future}

class SystemsController(systemsDao: SystemsDaoTrait)(implicit val ec: ExecutionContext) extends SystemsHandler {

  def getSystemList(respond: SystemsResource.getSystemListResponse.type)(): Future[SystemsResource.getSystemListResponse] = {
    systemsDao.getAllSystems map { all =>
      val asWire = all.map(_.toWire)(breakOut): IndexedSeq[WireSystem]
      respond.OK(asWire)
    }
  }

  def getSystemById(respond: SystemsResource.getSystemByIdResponse.type)(systemId: BigDecimal): Future[SystemsResource.getSystemByIdResponse] = {
    systemsDao.getSystemById(systemId.toInt) map {
      case None => respond.NotFound
      case Some(system: DbSystem) => respond.OK(system.toWire)
    }
  }
}
