package dataSources
import domains._
import anorm._
import anorm.SqlParser._
import javax.inject.Inject
import play.api.db.{DBApi, Database}

object WebStorage {

  def store(id: String, url: String)(implicit db: Database): Unit = {
    db.withConnection { implicit connection =>
      SQL(s"insert into web (id, url) values (\'${id}\', \'${url}\')").executeUpdate()
    }
  }

  def resolve(id: String)(implicit db: Database): Option[Web] = {
    val parser = str("id") ~ str("url")
    val mapper = parser.map {
      case id ~ url => Map("id" -> id, "url" -> url)
    }
    val res: Option[Map[String, String]] = db.withConnection { implicit connection =>
      SQL(s"select * from web where id == ${id}").as(mapper.singleOpt)
    }
    res.map { web =>
      Web(WebId(web("id")), web("url"))
    }
  }

  def selectAll()(implicit db: Database): List[Web] = {
    val parser = str("id") ~ str("url")
    val mapper = parser.map {
      case id ~ url => Map("id" -> id, "url" -> url)
    }
    val res: List[Map[String, String]] = db.withConnection { implicit connection =>
      SQL("select * from web").as(mapper.*)
    }
    res.map { web =>
      Web(WebId(web("id")), web("url"))
    }
  }

}

