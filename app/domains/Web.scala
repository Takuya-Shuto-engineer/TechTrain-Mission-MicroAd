package domains
import dataSources._
import play.api.db.{DBApi, Database}

class Web(val id: String, val url: String) {

  // 新規投稿
  def register()(implicit db: Database): Unit =
    WebHandler.store(this.id, this.url)

}

object Web {

  def apply(id: String, url: String): Web = {
    new Web(id, url)
  }

}

object WebService {

  def list()(implicit db: Database): List[Web] =
    WebHandler.selectAll()

}

