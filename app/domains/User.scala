package domains
import dataSources._
import play.api.db.{DBApi, Database}
import javax.inject.Inject

// モデル

case class UserId(val value: String) extends Identifier[String]

class User(val id: UserId, val browser: String) extends Entity[UserId]

object User {

  def apply(id: UserId, browser: String): User = {
    new User(id, browser)
  }

}

// レポジトリ

class UserRepository @Inject()(dbapi: DBApi) extends Repository[UserId, User] {

  implicit val db: Database = dbapi.database("default")

  def resolve(id: UserId): Option[User] = {
    // アクセスして特定のユーザのレコードを抜き出す
    UserStorage.resolve(id.value)
  }

  def store(user: User): Unit = {
    UserStorage.store(user.id.value, user.browser)
  }

  def list(): List[User] = UserStorage.selectAll()

}

// サービス
class UserService @Inject()(userRepository: UserRepository) extends Service[User, UserId] {

  def find(id: UserId): Option[User] = userRepository.resolve(id)

  def save(user: User): Unit = userRepository.store(user)

  def getList(): List[User] = userRepository.list()

}



