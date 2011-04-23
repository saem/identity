package jane.identity

import org.scalatra._
import org.scalatra.test.scalatest._
import org.scalatest.FlatSpec
import org.scalatest.matchers._

import java.io.File

class IdentityFilterSuite extends FlatSpec with ScalatraSuite with ShouldMatchers {
  addFilter(classOf[IdentityFilter], "/*")

  "The identity server" should "list basic info about identity server on /, and nothing else" in {
    get("/") {
      status should equal (200)
      header("Content-Type") should startWith ("application/json")
      body should equal ("{\n  \"who_am_i\":\"I\'m Rick James, BITCH!\"\n}")
    }

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

  it should "Manage catalogues for the identity server" in {
    val cleanDir = new DirectoryPlus(new File("/tmp/identityServer"))
    cleanDir.deleteAll

    get("/catalogues") {
      status should equal (204)
    }

    post("/catalogues", body = "{ \"name\" : \"newCatalogue\" }") {
      status should equal (302)
    }

    get("/catalogues") {
      status should equal (200)
      header("Content-Type") should startWith ("application/json")
      body should startWith ("[{\n  \"name\":\"newCatalogue\",\n  \"token\":\"newCatalogue")
    }

    cleanDir.deleteAll
  }
}