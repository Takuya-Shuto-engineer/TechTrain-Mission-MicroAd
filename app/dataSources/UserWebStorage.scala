package dataSources
import anorm._
import play.api.db._

object UserWebStorage {

  def increment(cookie: String, url: String)(implicit db: Database): Unit = {
    db.withConnection { implicit connection =>
      // 存在しない時は新規登録だから一つ目のsql文が実行される
      SQL(s"insert into user_web (cookie, url) select (\'${cookie}\', \'${url}\') where not exists (select 1 from user_web where cookie = ${cookie} and url = ${url}))").executeUpdate()
      SQL(s"update user_web set count = count+1 where cookie = \'${cookie}\' and url = \'${url}\'").executeUpdate()
    }
  }

}
