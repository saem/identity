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
