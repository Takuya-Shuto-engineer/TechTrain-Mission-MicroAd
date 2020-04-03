package domains
import dataSources._
import play.api.db.{DBApi, Database}

class User(val id: String, val browser: String) {

  // 新規投稿
  def register()(implicit db: Database): Unit =
    UserHandler.store(this.id, this.browser)

}

object User {

  def apply(id: String, browser: String): User = {
    new User(id, browser)
  }

}
