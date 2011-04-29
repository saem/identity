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

abstract class Model
case class BasicInfo(val who_am_i: String) extends Model
case class Catalogue(name: String, token: Option[String]) extends Model
case class Realm(name: String, catalogue: Option[Catalogue]) extends Model
case class Principal(name: String, token: Option[String], realm: Realm) extends Model

//Do I really need a repository and a DAO abstraction?
class CatalogueRepository(dao: CatalogueDao) {
  def findAll(): Option[Seq[Catalogue]] = {
    dao.loadAll()
  }

  def findByName(name: String): Option[Catalogue] = {
    dao.load(name)
  }

  def save(cat: Catalogue) = {
    dao.save(cat)
  }

  def exists(name: String) = {
    dao.exists(name)
  }

  def delete(name: String) = {
    dao.deleteByName(name)
  }
}

trait CatalogueDao {
  def loadAll(): Option[Seq[Catalogue]]
  def save(catalogue: Catalogue): Option[Catalogue]
  def load(name: String): Option[Catalogue]
  def exists(name: String): Boolean
  def deleteByName(name: String): Unit

  protected def generateToken(catalogue: Catalogue) = {
    catalogue.name + System.nanoTime.toString
  }
} 
