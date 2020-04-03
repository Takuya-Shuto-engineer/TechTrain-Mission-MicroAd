package domains

class UserWeb private (val id: String, val user: User, val web: Web, val count: Int) {

  // 新規投稿
  //def register(): Unit =
    //UserWebHandler.store(this.id, this.user.id, this.web.id, this.count)
}

object UserWeb {

  def apply(id: String, user: User, web: Web, count: Int = 0): UserWeb = {
    new UserWeb(id, user, web, count)
  }

}
