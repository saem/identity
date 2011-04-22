package jane.identity

import net.liftweb.json._
import net.liftweb.json.Extraction._
import net.liftweb.json.JsonDSL._
import net.liftweb.json.JsonAST._

import org.scalatra.ScalatraServlet

class IdentityServlet extends ScalatraServlet {
  implicit val formats = net.liftweb.json.DefaultFormats
  before {
    contentType = "application/json"
  }

  get("/") {
    pretty(render(decompose(BasicInfo("I'm Rick James, BITCH!"))))
  }
}

case class BasicInfo(val who_am_i: String)

case class Catalogue(name: String, token: String)

trait CatalogueRepository {
  def findAll(): Option[List[Catalogue]]
}