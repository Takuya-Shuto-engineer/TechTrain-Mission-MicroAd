package controllers

import javax.inject.Inject
import play.Logger
import play.api.mvc._
import play.api.libs.Codecs
import java.security.MessageDigest
// Java8 lib
import java.util.Base64

class TrackController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {
  val COOKIE_KEY = "3RD_PARTY_COOKIE_ID"
  val COOKIE_MAX_AFTER_AGE = Some(31622400 * 2)

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
    Ok(onePixelGifBytes).withCookies(Cookie(COOKIE_KEY, cookieValue, COOKIE_MAX_AFTER_AGE)).as("image/gif")
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