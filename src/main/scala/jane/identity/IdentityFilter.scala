/**
 * Copyright 2011 Saem Ghani
 * 
 * This file is part of Identity.
 * 
 * Identity is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * Identity is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with Identity. If not, see 
 * http://www.gnu.org/licenses/.
 */

package jane.identity

import net.liftweb.json._
import net.liftweb.json.Extraction._
import net.liftweb.json.JsonParser._
import net.liftweb.json.JsonDSL._
import net.liftweb.json.JsonAST._

import org.scalatra.ScalatraFilter

class IdentityFilter extends ScalatraFilter with Configuration {
  implicit val formats = net.liftweb.json.DefaultFormats
  before {
    contentType = "application/json"
  }

  notFound {
    halt(404)
  }

  get("/") {
    jsonify(BasicInfo("I'm Rick James, BITCH!"))
  }

  post("/catalogues") {
    val catalogue = parse(request.body).extract[Catalogue]
    def writeCatalogue(catalogue: Catalogue) {
      catalogueRepository.save(catalogue)
      redirect("/catalogues/" + catalogue.name)
    }
    catalogueRepository.findByName(catalogue.name) match {
      case None => writeCatalogue(catalogue)
      case _ => if(params.getOrElse("overwrite", "false").toLowerCase == "true") {
                  writeCatalogue(catalogue)
                } else {
                  halt(409, "A catalogue with the same name already exists.")
                }
    }
  }

  get("/catalogues") {
    val catalogues = catalogueRepository.findAll()
    catalogues match {
      case None => halt(204)
      case c: Some[Seq[Catalogue]] => if(!c.isEmpty) { jsonify(c) } else { halt(204) }
    }
  }

  put("/catalogues/:catalogue") {
    //I should probably do this through a map or some such
    val existed = catalogueRepository.exists(params("catalogue"))
    val catalogue = catalogueRepository.save(Catalogue(params("catalogue")))
    catalogue match {
      case cat: Some[Catalogue] => if(!existed) {
                                     response.setStatus(201)
                                   }
                                   jsonify(cat.get)
      case _ => halt(500)
    }
  }

  delete("/catalogues/:catalogue") {
    catalogueRepository.delete(params("catalogue"))
    response.setStatus(204)
  }

  protected def jsonify(a: Any): String = {
    pretty(render(decompose(a)))
  }
}

import java.io._

trait Configuration {
  self : ScalatraFilter =>

  private val catalogueDao = new CatalogueFileBackedDao(new FilePlus(new File("/tmp/identityServer/")))
  val catalogueRepository = new CatalogueRepository(catalogueDao)
}