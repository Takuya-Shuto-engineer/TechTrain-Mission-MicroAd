package domains
import dataSources._
import javax.inject.Inject
import play.api.db.{DBApi, Database}

// モデル

case class Url(val value: String) extends Identifier[String]

class Web(val id: Url, val advertiser: String) extends Entity[Url]

object Web {

  def apply(id: Url, advertiser: String): Web = {
    new Web(id, advertiser)
  }

}

// レポジトリ

class WebRepository @Inject()(dbapi: DBApi) extends Repository[Url, Web] {

  implicit val db: Database = dbapi.database("default")

  def resolve(url: Url): Option[Web] = {
    // 特定のWebのレコードを抜き出す
    WebStorage.resolve(url.value)
  }

  def store(web: Web): Unit = {
    // Webの情報を新規に登録する
    WebStorage.store(web.id.value, web.advertiser)
  }

  def list(): List[Web] = WebStorage.selectAll()

}

// サービス
class WebService @Inject()(webRepository: WebRepository) extends Service[Web, Url] {

  def find(url: Url): Option[Web] = webRepository.resolve(url)

  def save(web: Web): Unit = webRepository.store(web)

  def getList(): List[Web] = webRepository.list()

}
