package controllers

import javax.inject.Inject
import play.Logger
import play.api.mvc._
import play.api.libs.Codecs
import java.security.MessageDigest

import scala.util.parsing.json.{JSONArray, JSONObject}
import java.util.Base64

import domains._

import scala.concurrent._
import ExecutionContext.Implicits.global

class TrackController @Inject()(cc: ControllerComponents, userService: UserService, webService: WebService) extends AbstractController(cc) {
  val COOKIE_KEY = "3RD_PARTY_COOKIE_ID"
  val COOKIE_MAX_AFTER_AGE = Some(31622400 * 2)


  // 参考: https://css-tricks.com/snippets/html/base64-encode-of-1x1px-transparent-gif/
  val onePixelGifBase64 = "R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7"
  val onePixelGifBytes = Base64.getDecoder().decode(onePixelGifBase64)

  def pixelTracking = Action { request =>
    val cookies = request.cookies
    val url = Url(request.headers("referer"))
    val browser = request.headers("User-Agent")
    Logger.debug(s"$browser")

    // 登録済みのwebかどうかをチェック
    val web: Web = webService.find(url).getOrElse{
      val advertiser: String = "Unknown"
      val web: Web = Web(url,advertiser)
      Future(webService.save(web)) // web新規登録
      web
    }

    // 登録済みユーザかチェック
    val cookieValue = cookies.get(COOKIE_KEY).map { cookie =>
      Logger.debug(s"Cookie Exists! ${cookie.value}")
      userService.find(UserCookie(cookie.value)).getOrElse{
        // Cookieを持っているのに登路されていない場合は再登録
        newUserRegister(cookie.value, browser)
      }
      cookie.value
    }.getOrElse {
      // Cookieを持っていないなら発行してユーザ登録
      val newCookie = uniqueIdGenerator()
      newUserRegister(newCookie, browser)
      newCookie
    }

    val user: User = User(UserCookie(cookieValue), browser)

    Future(userService.visit(user, web)) // 訪問を記録

    val count: Int = userService.getCount(user, web).getOrElse(0)

    Logger.debug(s"This User visited this website ${count} times.")

    Ok(onePixelGifBytes).withCookies(Cookie(COOKIE_KEY, cookieValue, COOKIE_MAX_AFTER_AGE)).as("image/gif")
  }

  def getWebList = Action.async {
    val listFuture: Future[List[Web]] = Future(webService.getList())

    listFuture.map { list =>
      val webListMap = list.map { web =>
        Map(
          "url" -> web.id.value,
          "advertiser" -> web.advertiser
        )
      }
      val webListJson = JSONArray(webListMap.map(JSONObject))
      Ok(JSONObject(Map("Web" -> webListJson)).toString()).as("application/json")
    }
  }

  def getUserList = Action.async {
    val listFuture: Future[List[User]] = Future(userService.getList())

    listFuture.map { list =>
      val userListMap = list.map { user =>
        Map(
          "cookie" -> user.id.value,
          "browser" -> user.browser
        )
      }
      val userListJson = JSONArray(userListMap.map(JSONObject))
      Ok(JSONObject(Map("User" -> userListJson)).toString()).as("application/json")
    }
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

  val newUserRegister = (cookie: String, browser: String) =>  {
    Logger.debug(s"Cookie Generate! $cookie")
    val newUser: User = User(UserCookie(cookie), browser)
    Future(userService.save(newUser)) // ユーザ新規登録
    newUser
  }

}