package dataSources
import domains._
import anorm._
import anorm.SqlParser._
import play.api.db.Database

object UserStorage {

  def store(id: String, browser: String)(implicit db: Database): Unit = {
    db.withConnection { implicit connection =>
      SQL(s"insert into user (id, browser) values (\'${id}\', \'${browser}\')").executeUpdate()
    }
  }

  def resolve(id: String)(implicit db: Database): Option[User] = {
    val parser = str("id") ~ str("browser")
    val mapper = parser.map {
      case id ~ browser => Map("id" -> id, "browser" -> browser)
    }
    val res: Option[Map[String, String]] = db.withConnection { implicit connection =>
      SQL(s"select * from user where id == ${id}").as(mapper.singleOpt)
    }
    res.map { user =>
      User(UserId(user("id")), user("browser"))
    }
  }

  def selectAll()(implicit db: Database): List[User] = {
    val parser = str("id") ~ str("browser")
    val mapper = parser.map {
      case id ~ browser => Map("id" -> id, "browser" -> browser)
    }
    val res: List[Map[String, String]] = db.withConnection { implicit connection =>
      SQL("select * from user").as(mapper.*)
    }
    res.map { user =>
      User(UserId(user("id")), user("browser"))
    }
  }

}
