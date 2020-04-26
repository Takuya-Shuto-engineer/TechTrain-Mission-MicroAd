package dataSources
import domains._
import anorm._
import anorm.SqlParser._
import javax.inject.Inject
import play.api.db.{DBApi, Database}

object WebStorage {

  def store(url: String, advertiser: String)(implicit db: Database): Unit = {
    db.withConnection { implicit connection =>
      SQL(s"insert into web (url, advertiser) values (\'${url}\', \'${advertiser}\')").executeUpdate()
    }
  }

  def resolve(url: String)(implicit db: Database): Option[Web] = {
    val parser = int("id") ~ str("url") ~ str("advertiser")
    val mapper = parser.map {
      case id ~ url ~ advertiser => Map("id" -> id, "url" -> url, "advertiser" -> advertiser)
    }
    val res: Option[Map[String, Any]] = db.withConnection { implicit connection =>
      SQL(s"select * from web where url = \'${url}\'").as(mapper.singleOpt)
    }
    res.map { web =>
      Web(Url(web("url").toString), web("advertiser").toString)
    }
  }

  def selectAll()(implicit db: Database): List[Web] = {
    val parser = int("id") ~ str("url") ~ str("advertiser")
    val mapper = parser.map {
      case id ~ url ~ advertiser => Map("id" -> id, "url" -> url, "advertiser" -> advertiser)
    }
    val res: List[Map[String, Any]] = db.withConnection { implicit connection =>
      SQL("select * from web").as(mapper.*)
    }
    res.map { web =>
      Web(Url(web("url").toString), web("advertiser").toString)
    }
  }

}

