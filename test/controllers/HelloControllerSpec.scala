package controllers

import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test._

class HelloControllerSpec extends PlaySpec with GuiceOneAppPerTest {

  "HelloController GET" should {

    "「/hello」にGETメソッドでアクセスすると「Hello World」が返る"  in {
      val request = FakeRequest(GET, "/hello")
      val home = route(app, request).get

      status(home) mustBe OK
      contentType(home) mustBe Some("text/plain")
      contentAsString(home) mustBe "Hello World"
    }
  }

  "HelloController POST" should {

    "「/hello」にPOSTメソッドでアクセスするとJsonが返る"  in {
      val request = FakeRequest(POST, "/hello")
      val home = route(app, request).get

      status(home) mustBe OK
      contentType(home) mustBe Some("application/json")
      contentAsJson(home) mustBe Json.obj("hello"-> "world", "language" -> "scala")
    }
  }
}