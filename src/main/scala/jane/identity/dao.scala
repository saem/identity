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

import java.io._

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
    if(!tmpFile.exists) {
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
    if(exists(name)) {
      val file = new File(dataDir, name)
      Some(file2Catalogue(file))
    } else {
      None
    }
  }

  def exists(name: String) = {
    val file = new File(dataDir, name)
    file.exists
  }

  def deleteByName(name: String) {
    val file: FilePlus = new File(dataDir, name)
    file.deleteAll
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