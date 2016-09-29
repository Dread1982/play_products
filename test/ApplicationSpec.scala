import org.scalatestplus.play._
import play.api.test._
import play.api.test.Helpers._

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */
class ApplicationSpec extends PlaySpec with OneAppPerTest {

  "Routes" should {

    "send 404 on a bad request" in {
      route(app, FakeRequest(GET, "/boum")).map(status(_)) mustBe Some(NOT_FOUND)
    }

  }

  "HomeController" should {

    "redirect to products page" in {
      val home = route(app, FakeRequest(GET, "/")).get

      status(home) mustBe SEE_OTHER
      redirectLocation(home).get mustBe ("/products")
    }

  }

  "CountController" should {

    "return an increasing count" in {
      contentAsString(route(app, FakeRequest(GET, "/count")).get) mustBe "0"
      contentAsString(route(app, FakeRequest(GET, "/count")).get) mustBe "1"
      contentAsString(route(app, FakeRequest(GET, "/count")).get) mustBe "2"
    }

  }

  "ProductsContoller" should {
    "show list page" in {
      val list = route(app, FakeRequest(GET, "/products")).get

      status(list) mustBe OK
      contentType(list).get mustBe "text/html"
      contentAsString(list) must include("Product catalog")
    }

    "show details page" in {
      val details = route(app, FakeRequest(GET, "/products/5010255079763")).get

      status(details) mustBe OK
      contentType(details).get mustBe "text/html"
      contentAsString(details) must include("Paperclips")
    }
    
    "save a product" in {
      val save = route(app, FakeRequest(POST, "/products")).get

      status(save) mustBe SEE_OTHER
      redirectLocation(save).get mustBe ("/products/new")
    }
  }
}
