package jane.identity

import org.scalatra._
import org.scalatra.test.scalatest._
import org.scalatest.WordSpec
import org.scalatest.matchers._

import java.io.File

class IdentityFilterSuite extends WordSpec with ScalatraSuite with ShouldMatchers {
  addFilter(classOf[IdentityFilter], "/*")

  "The identity server" should {
    "list basic info about identity server on /, and nothing else" in {
      get("/") {
        status should equal (200)
        header("Content-Type") should startWith ("application/json")
        body should equal ("{\n  \"who_am_i\":\"I\'m Rick James, BITCH!\"\n}")
      }
    }
    "and 404 on the other methods" in {
      post("/") {
        status should equal (404)
      }
      put("/") {
        status should equal (404)
      }
      delete("/") {
        status should equal (404)
      }
    }

    "provide management for catalogues such as" when {
      val cleanDir = new FilePlus(new File("/tmp/identityServer"))
      cleanDir.deleteAll

      "reading, /catalogues when there are none, should indicated a 204" in {
        get("/catalogues") {
          status should equal (204)
        }
      }

      "writing, by posting to /catalogues, which should create and redirect to the new catalogue" in {
        post("/catalogues", body = "{ \"name\" : \"newCatalogue\" }") {
          status should equal (302)
          header("Location") should endWith ("/catalogues/newCatalogue")
        }
      }

      "reading after a successful write should get a 200 as there is at least one catalogue" in {
        get("/catalogues") {
          status should equal (200)
          header("Content-Type") should startWith ("application/json")
          body should startWith ("[{\n  \"name\":\"newCatalogue\",\n  \"token\":\"newCatalogue")
        }
      }

      "writing a new catalogue where one already exists" should {
        "throws a 409 Conflict error" in {
          post("/catalogues", body = "{ \"name\" : \"newCatalogue\" }") {
            status should equal (409)
            body should include ("A catalogue with the same name already exists.")
          }
        }
        //TODO: Update this test to test against metadata once that's supported
        "unless you pass in the overwrite parameter as true" in {
          post("/catalogues?overwrite=true", body = "{ \"name\" : \"newCatalogue\" }") {
            status should equal (302)
            header("Location") should endWith ("/catalogues/newCatalogue")
          }
        }
      }

      "updating an existing catalogue" should {
        "return 200" in {
          put("/catalogues/newCatalogue", body = "{\"name\":\"aCatalogue\"}") {
            body should startWith ("{\n  \"name\":\"newCatalogue\",\n  \"token\":\"newCatalogue")
            status should equal (200)
          }
        }
        "otherwise, create a new one and return 201" in {
          put("/catalogues/aCatalogue", body = "{\"name\":\"aCatalogue\"}") {
            status should equal (201)
            body should startWith ("{\n  \"name\":\"aCatalogue\",\n  \"token\":\"aCatalogue")
          }
        }
      }

      "delete a catalogue return 204 no content" in {
        delete("/catalogues/newCatalogue") {
          status should equal (204)
          body should equal ("")
        }
        get("/catalogues/newCatalogue") {
          status should equal (404)
        }
      }

      cleanDir.deleteAll
    }
  }
}