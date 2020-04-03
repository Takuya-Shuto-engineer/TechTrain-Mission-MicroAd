package dataSources
import domains._
import anorm._
import anorm.SqlParser._
import javax.inject.Inject
import play.api.db.Database

object UserHandler {

  def store(id: String, browser: String)(implicit db: Database): Unit = {
    db.withConnection { implicit connection =>
      SQL(s"insert into User (id, browser) values (\'${id}\', \'${browser}\')").executeUpdate()
    }
  }

}


