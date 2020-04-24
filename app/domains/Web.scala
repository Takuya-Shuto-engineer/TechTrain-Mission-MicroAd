package domains
import dataSources._
import javax.inject.Inject
import play.api.db.{DBApi, Database}

// モデル

case class WebId(val value: String) extends Identifier[String]

class Web(val id: WebId, val url: String) extends Entity[WebId]

object Web {

  def apply(id: WebId, url: String): Web = {
    new Web(id, url)
  }

}

// レポジトリ

class WebRepository @Inject()(dbapi: DBApi) extends Repository[WebId, Web] {

  implicit val db: Database = dbapi.database("default")

  def resolve(id: WebId): Option[Web] = {
    // 特定のWebのレコードを抜き出す
    WebStorage.resolve(id.value)
  }

  def store(web: Web): Unit = {
    // Webの情報を新規に登録する
    WebStorage.store(web.id.value, web.url)
  }

  def list(): List[Web] = WebStorage.selectAll()

}

// サービス
class WebService @Inject()(webRepository: WebRepository) extends Service[Web, WebId] {

  def find(id: WebId): Option[Web] = webRepository.resolve(id)

  def save(web: Web): Unit = webRepository.store(web)

  def getList(): List[Web] = webRepository.list()

}
