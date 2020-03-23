package controllers

import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test._

class HelloControllerSpec extends PlaySpec with GuiceOneAppPerTest {

  "HelloController GET" should {

    "「/hello」にGETメソッドでアクセスすると「Hello user hoge」が返る"  in {
      val request = FakeRequest(GET, "/hello")
      val home = route(app, request).get

      status(home) mustBe OK
      contentType(home) mustBe Some("text/plain")
    }
  }

}