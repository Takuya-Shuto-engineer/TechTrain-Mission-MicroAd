package dataSources
import anorm.SqlParser.{int, str}
import anorm._
import play.api.db._

object UserWebStorage {

  def increment(cookie: String, url: String)(implicit db: Database): Unit = {
    db.withConnection { implicit connection =>
      // 存在しない時は新規登録だから一つ目のsql文が実行される
      SQL(s"insert into user_web (cookie, url) select (\'${cookie}\', \'${url}\') where not exists (select * from user_web where cookie = ${cookie} and url = ${url}))").executeUpdate()
      SQL(s"update user_web set count = count+1 where cookie = \'${cookie}\' and url = \'${url}\'").executeUpdate()
    }
  }

  def count(cookie: String, url: String)(implicit db: Database): Option[Int] = {
    val parser = int("id") ~ str("cookie") ~ str("url") ~ int("count")
    val mapper = parser.map {
      case id ~ cookie ~ url ~ count => Map("id" -> id, "cookie" -> cookie, "url" -> url, "count" -> count)
    }
    val res: Option[Map[String, Any]] = db.withConnection { implicit connection =>
      SQL(s"select * from user_web where cookie = \'${cookie}\' and url = \'${url}\'").as(mapper.singleOpt)
    }
    res.map { web =>
      web("count").toString.toInt
    }
  }

}
