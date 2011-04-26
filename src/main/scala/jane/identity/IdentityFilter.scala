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
import net.liftweb.json.JsonDSL._
import net.liftweb.json.JsonAST._
import net.liftweb.json.JsonParser._

import java.io._

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
    pretty(render(decompose(BasicInfo("I'm Rick James, BITCH!"))))
  }

  post("/catalogues") {
    val catalogue = parse(request.body).extract[Catalogue]
    catalogueRepository.findByName(catalogue.name) match {
      case None => catalogueRepository.save(catalogue)
                redirect("/catalogues/" + catalogue.name)
      case _ => halt(409, "A catalogue with the same name already exists.")
    }
  }

  get("/catalogues") {
    val catalogues = catalogueRepository.findAll()
    catalogues match {
      case None => halt(204)
      case c: Some[Seq[Catalogue]] => if(!c.isEmpty) { pretty(render(decompose(c))) } else { halt(204) }
    }
  }
}

trait Configuration {
  self : ScalatraFilter =>

  private val catalogueDao = new CatalogueFileBackedDao(new FilePlus(new File("/tmp/identityServer/")))
  val catalogueRepository = new CatalogueRepository(catalogueDao)
}

case class BasicInfo(val who_am_i: String)
case class Catalogue(name: String, token: Option[String])
case class Realm(name: String, catalogue: Option[Catalogue])
case class Principal(name: String, token: Option[String], realm: Realm)

class CatalogueRepository(dao: CatalogueDao) {
  def findAll(): Option[Seq[Catalogue]] = {
    dao.loadAll()
  }

  def findByName(name: String): Option[Catalogue] = {
    dao.load(name)
  }

  def save(cat: Catalogue) {
    dao.save(cat)
  }
}

trait CatalogueDao {
  def loadAll(): Option[Seq[Catalogue]]
  def save(catalogue: Catalogue): Option[Catalogue]
  def load(name: String): Option[Catalogue]

  protected def generateToken(catalogue: Catalogue) = {
    catalogue.name + System.nanoTime.toString
  }
}

class CatalogueFileBackedDao(val dataDir: FilePlus) extends CatalogueDao {
  //implicit conversions
  import FilePlus._
  import CatalogueFileBackedDao._

  lazy val tempDir: FilePlus = {
    val dir = new File(dataDir, "tmp/")
    dir.mkdirs
    dir
  }

  def loadAll(): Option[Seq[Catalogue]] = {
    val files = dataDir.listFiles
    files match {
      case files: Array[File] =>
        if(files.length > 0) {
          import CatalogueFileBackedDao._
          Some(files filter { (file) => (!file.isDirectory && !file.isHidden) } map { f => file2Catalogue(f) })
        } else {
          None
        }
      case _ => None
    }
  }

  def save(catalogue: Catalogue): Option[Catalogue] = {
    val tmpFile = new File(tempDir, catalogue.name)
    val finalFile = new File(dataDir, catalogue.name) //Relying on the FS providing atomic moves
    if(!tmpFile.exists && !finalFile.exists) {
      val cat = catalogue.token match {
        case None => Catalogue(catalogue.name, Option(generateToken(catalogue)))
        case _ => catalogue
      }
      tmpFile.write(cat)
      tmpFile.renameTo(finalFile)
      Some(cat)
    } else {
      None
    }
  }

  def load(name: String): Option[Catalogue] = {
    val file = new File(dataDir, name)
    if(file.exists) { Some(file2Catalogue(file)) } else None
  }
}
object CatalogueFileBackedDao {
  implicit val formats = net.liftweb.json.DefaultFormats

  implicit def file2Catalogue(file: FilePlus) = {
    parse(file.readLines).extract[Catalogue]
  }
  implicit def catalogue2JsonString(cat: Catalogue): String = {
    pretty(render(decompose(cat)))
  }
}