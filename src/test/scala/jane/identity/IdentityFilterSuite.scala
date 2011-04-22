package jane.identity

import org.scalatra._
import org.scalatra.test.scalatest._
import org.scalatest.matchers._

class IdentityFilterSuite extends ScalatraFunSuite with ShouldMatchers {
  addServlet(classOf[IdentityServlet], "/*")

  test("List basic info about identity server") {
    get("/") {
      status should equal (200)
      header("Content-Type") should startWith ("application/json")
      body should equal ("{\n  \"who_am_i\":\"I\'m Rick James, BITCH!\"\n}")
    }
  }
}