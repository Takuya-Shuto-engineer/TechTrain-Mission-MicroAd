package dataSources
import domains._
import anorm._
import anorm.SqlParser._
import play.api.db.Database

object UserStorage {

  def store(cookie: String, browser: String)(implicit db: Database): Unit = {
    db.withConnection { implicit connection =>
      SQL(s"insert into user (cookie, browser) values (\'${cookie}\', \'${browser}\')").executeUpdate()
    }
  }

  def resolve(cookie: String)(implicit db: Database): Option[User] = {
    val parser = int("id") ~ str("cookie") ~ str("browser")
    val mapper = parser.map {
      case id ~ cookie ~ browser => Map("id" -> id, "cookie" -> cookie, "browser" -> browser)
    }
    val res: Option[Map[String, Any]] = db.withConnection { implicit connection =>
      SQL(s"select * from user where cookie = \'${cookie}\'").as(mapper.singleOpt)
    }
    res.map { user =>
      User(UserCookie(user("cookie").toString), user("browser").toString)
    }
  }

  def selectAll()(implicit db: Database): List[User] = {
    val parser = int("id") ~ str("cookie") ~ str("browser")
    val mapper = parser.map {
      case id ~ cookie ~ browser => Map("id" -> id, "cookie" -> cookie, "browser" -> browser)
    }
    val res: List[Map[String, Any]] = db.withConnection { implicit connection =>
      SQL("select * from user").as(mapper.*)
    }
    res.map { user =>
      User(UserCookie(user("cookie").toString), user("browser").toString)
    }
  }

}


