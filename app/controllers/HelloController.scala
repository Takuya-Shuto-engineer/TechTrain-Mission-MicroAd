package controllers

import javax.inject.Inject
import play.api.libs.json._
import play.api.mvc._



class HelloController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {
  // def hello(): Action[AnyContent] = Action(Ok("Hello World"))
  def hello(): Action[AnyContent] = Action{
    request => {
      val uuid: String = request.cookies.get("userId").map(_.value).getOrElse(java.util.UUID.randomUUID.toString) // CookieにIDがなければ生成
      val count: String = request.cookies.get("count").map(_.value).getOrElse("0") // cookieにカウントがない初見さんは0回で初期化
      val currentCount: String = (count.toInt + 1).toString
      val response: String = s"Hello user $uuid" + s"\nYou've been here for $currentCount times"
      Ok(response).withCookies(Cookie("userId", uuid), Cookie("count", currentCount))
    }
  }

  def helloJson():Action[AnyContent] = Action{
    request => {
      val uuid: String = request.cookies.get("userId").map(_.value).getOrElse(java.util.UUID.randomUUID.toString) // hello()と一緒
      val count: String = request.cookies.get("count").map(_.value).getOrElse("0") // hello()と一緒
      val currentCount: String = (count.toInt + 1).toString
      val json: JsValue = Json.obj("hello" -> s"user$uuid", "language" -> "scala", "count" -> currentCount)
      Ok(json).withCookies(Cookie("userId", uuid), Cookie("count", currentCount))
    }
  }
}