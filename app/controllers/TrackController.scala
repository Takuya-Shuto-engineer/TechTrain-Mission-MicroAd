package controllers

import javax.inject.Inject
import play.Logger
import play.api.mvc._
import play.api.libs.Codecs
import java.security.MessageDigest
import play.api.db.Database
import scala.util.parsing.json.{ JSONArray, JSONObject }
import java.util.Base64
import domains._
import play.api.db.DBApi

class TrackController @Inject()(cc: ControllerComponents, dbapi: DBApi) extends AbstractController(cc) {
  val COOKIE_KEY = "3RD_PARTY_COOKIE_ID"
  val COOKIE_MAX_AFTER_AGE = Some(31622400 * 2)
  implicit val db: Database = dbapi.database("default")

  // 参考: https://css-tricks.com/snippets/html/base64-encode-of-1x1px-transparent-gif/
  val onePixelGifBase64 = "R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7"
  val onePixelGifBytes = Base64.getDecoder().decode(onePixelGifBase64)

  def pixelTracking = Action { request =>
    val cookies = request.cookies
    val cookieValue = cookies.get(COOKIE_KEY).map { cookie =>
      Logger.debug(s"Cookie Exists! ${cookie.value}")
      cookie.value
    }.getOrElse {
      val newValue = uniqueIdGenerator()
      Logger.debug(s"Cookie Generate! $newValue")
      newValue
    }
    val url = request.headers("referer")
    val browser = request.headers("User-Agent")
    Logger.debug(s"$browser")
    val user: User = User(cookieValue, browser)
    user.register() // ユーザ登録
    val web: Web = Web(uniqueIdGenerator(), url)
    web.register() // web登録
    Ok(onePixelGifBytes).withCookies(Cookie(COOKIE_KEY, cookieValue, COOKIE_MAX_AFTER_AGE)).as("image/gif")
  }

  def getWebList = Action {
    val list: List[Web] = WebService.list()
    val webListMap = list.map { web =>
      val urlEscaped = web.url
      Map(
        "id" -> web.id,
        "url" -> urlEscaped
      )
    }
    // Jsonでposts一覧を返す
    val webListJson = JSONArray(webListMap.map(JSONObject))
    Ok(JSONObject(Map("Web" -> webListJson)).toString()).as("application/json")
  }

  val uniqueIdGenerator = () => {
    val milliTime = System.currentTimeMillis()
    val nanoTime = System.nanoTime()
    val baseString = s"$milliTime $nanoTime"
    Logger.debug(baseString)

    val md = MessageDigest.getInstance("SHA-256")
    md.update(baseString.getBytes())

    val id = Codecs.toHexString(md.digest())
    Logger.debug(id)
    id
  }

}