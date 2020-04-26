package domains
import dataSources._
import play.api.db.{DBApi, Database}
import javax.inject.Inject

// モデル

case class UserCookie(val value: String) extends Identifier[String]

class User(val id: UserCookie, val browser: String) extends Entity[UserCookie]

object User {

  def apply(id: UserCookie, browser: String): User = {
    new User(id, browser)
  }

}

// レポジトリ

class UserRepository @Inject()(dbapi: DBApi) extends Repository[UserCookie, User] {

  implicit val db: Database = dbapi.database("default")

  def resolve(cookie: UserCookie): Option[User] = {
    // アクセスして特定のユーザのレコードを抜き出す
    UserStorage.resolve(cookie.value)
  }

  def store(user: User): Unit = {
    UserStorage.store(user.id.value, user.browser)
  }

  def list(): List[User] = UserStorage.selectAll()

  def increment(cookie: UserCookie, url: Url): Unit = {
    UserWebStorage.increment(cookie.value, url.value)
  }

}

// サービス
class UserService @Inject()(userRepository: UserRepository) extends Service[User, UserCookie] {

  def find(id: UserCookie): Option[User] = userRepository.resolve(id)

  def save(user: User): Unit = userRepository.store(user)

  def getList(): List[User] = userRepository.list()

  def visit(user: User, web: Web): Unit = userRepository.increment(user.id, web.id)

}



