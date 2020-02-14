package controllers

import javax.inject.Inject
import play.api.libs.json._
import play.api.mvc._

class HelloController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  val gaId: String = sys.env.getOrElse("GA_ID", "default") // Google AnalyticsのID

  def hello(): Action[AnyContent] = Action{
    request => {
      val userId: String = request.cookies.get("userId").map(_.value).getOrElse(java.util.UUID.randomUUID.toString) // CookieにIDがなければ生成
      val count: String = request.cookies.get("count").map(_.value).getOrElse("0") // cookieにカウントがない初見さんは0回で初期化
      val currentCount: String = (count.toInt + 1).toString
      val response: String = s"Hello user $userId" + s"\nYou've been here for $currentCount times"
      Ok(views.html.hello(response, gaId)).withCookies(Cookie("userId", userId), Cookie("count", currentCount)).bakeCookies()
    }
  }

  def helloJson():Action[AnyContent] = Action{
    request => {
      val userId: String = request.cookies.get("userId").map(_.value).getOrElse(java.util.UUID.randomUUID.toString) // hello()と一緒
      val count: String = request.cookies.get("count").map(_.value).getOrElse("0") // hello()と一緒
      val currentCount: String = (count.toInt + 1).toString
      val json: JsValue = Json.obj("hello" -> s"user $userId", "language" -> "scala", "count" -> currentCount)
      Ok(json).withCookies(Cookie("userId", userId), Cookie("count", currentCount)).bakeCookies()
    }
  }
}