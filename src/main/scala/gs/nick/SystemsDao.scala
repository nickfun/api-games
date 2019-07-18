package gs.nick

import gs.nick.server.definitions.WireSystem
import slick.jdbc.MySQLProfile
import scala.concurrent.Future
import slick.jdbc.MySQLProfile.api._
import scala.concurrent.ExecutionContext.Implicits.global

case class DbSystem(
    id: Int,
    name: String,
    company: String,
    release: Option[String],
    comments: String,
    num: Int
) {

  def toWire: WireSystem = {
    WireSystem(
      Option(id),
      name,
      company,
      release,
      Option(comments),
      Option(num)
    )
  }
}

object DbSystem {

  def fromWire(wire: WireSystem): DbSystem = {
    DbSystem(
      wire.id.getOrElse(-1),
      wire.name,
      wire.company,
      wire.release,
      wire.comments.getOrElse(""),
      wire.num.getOrElse(-1)
    )
  }
}

class SystemsTable(tag: Tag) extends Table[DbSystem](tag, "systems") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def company = column[String]("company")
  def release = column[Option[String]]("release")
  def comments = column[String]("comments")
  def num = column[Int]("num")

  def * = (id, name, company, release, comments, num) <> ((DbSystem.apply _).tupled, DbSystem.unapply _)
}

object SystemsTable {
  val allSystems = TableQuery[SystemsTable]
}

class SystemsDao(db: MySQLProfile.backend.DatabaseDef) extends SystemsDaoTrait {
  override def getAllSystems: Future[Seq[DbSystem]] = {
    db.run(SystemsTable.allSystems.result)
  }

  override def getSystemById(sysid: Int): Future[Option[DbSystem]] = {
    db.run(SystemsTable.allSystems.filter(_.id === sysid).result).map(_.headOption)
  }

  override def addSystem(newSystem: DbSystem): Future[Int] = {
    val data = SystemsTable.allSystems
    val insertAction = (data returning data.map(_.id)) += newSystem
    db.run(insertAction)
  }
}
