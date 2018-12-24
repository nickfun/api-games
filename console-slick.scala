import gs.nick.DbGame
import gs.nick.GamesTable
import slick.jdbc.MySQLProfile.api._
import scala.concurrent.ExecutionContext.Implicits.global
GamesTable.allGames
val url = "jdbc:mysql://localhost/nickf1?serverTimezone=UTC&zeroDateTimeBehavior=convertToNull";
val db = Database.forURL(url, "root", "")
val query = GamesTable.allGames.filter(_.id < 5).result
val result = db.run(query)

println("=====")
