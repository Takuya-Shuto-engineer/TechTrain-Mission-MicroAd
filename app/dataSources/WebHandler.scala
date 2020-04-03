package dataSources
import domains._
import anorm._
import anorm.SqlParser._
import javax.inject.Inject
import play.api.db.Database

object WebHandler {

  def store(id: String, url: String)(implicit db: Database): Unit = {
    db.withConnection { implicit connection =>
      SQL(s"insert into Web (id, url) values (\'${id}\', \'${url}\')").executeUpdate()
    }
  }

  def selectAll()(implicit db: Database): List[Web] = {
    val parser = str("id") ~ str("url")
    val mapper = parser.map {
      case id ~ url => Map("id" -> id, "url" -> url)
    }
    val res: List[Map[String, String]] = db.withConnection { implicit connection =>
      SQL("select * from Web").as(mapper.*)
    }
    res.map { web =>
      Web(web("id"), web("url"))
    }
  }

}

