package gs.nick

import gs.nick.server.definitions.WireSystem
import gs.nick.server.systems.{SystemsHandler, SystemsResource}

import collection.breakOut
import scala.concurrent.{ExecutionContext, Future}

class SystemsController(systemsDao: SystemsDaoTrait)(implicit val ec: ExecutionContext) extends SystemsHandler {

  def getSystemList(respond: SystemsResource.getSystemListResponse.type)(): Future[SystemsResource.getSystemListResponse] = {
    println("get all systems")
    systemsDao.getAllSystems map { all =>
      val asWire = all.map(_.toWire)(breakOut): IndexedSeq[WireSystem]
      respond.OK(asWire)
    }
  }

  def getSystemById(respond: SystemsResource.getSystemByIdResponse.type)(systemId: BigDecimal): Future[SystemsResource.getSystemByIdResponse] = {
    println("get one system")
    systemsDao.getSystemById(systemId.toInt) map {
      case None => respond.NotFound
      case Some(system: DbSystem) => respond.OK(system.toWire)
    }
  }

  override def createSystem(respond: SystemsResource.createSystemResponse.type)(newSystem: WireSystem): Future[SystemsResource.createSystemResponse] = {
    println("create a system")
    val dbSystem = DbSystem.fromWire(newSystem)
    systemsDao.addSystem(dbSystem) map { newId =>
      val respondSystem = newSystem.copy(id = Some(newId))
      respond.OK(respondSystem)
    }
  }
}
