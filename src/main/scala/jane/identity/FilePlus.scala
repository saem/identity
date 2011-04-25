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

import java.io.{FileWriter, File}

/**
 * Created by IntelliJ IDEA.
 * User: gary
 * Date: 4/22/11
 * Time: 11:37 PM
 * To change this template use File | Settings | File Templates.
 */

//TODO: Separate this out and write some tests
class FilePlus(val file : File) {
  def write(text : String) {
    val fw = new FileWriter(file)
    try { fw.write(text) } finally { fw.close }
  }

  def readLines() = {
    io.Source.fromFile(file).mkString
  }
}
class DirectoryPlus(dir: File) {
  def deleteAll() {
    def deleteFile(dirToDelete : File) : Unit = {
      if(dirToDelete.isDirectory) {
        val listing = dirToDelete.listFiles
        if(listing != null) { listing.foreach{ dir => deleteFile(dir) } }
      }
      dirToDelete.delete
    }
    deleteFile(dir)
  }
}

object FilePlus {
  implicit def plusFile(file: File): FilePlus = new FilePlus(file)
  implicit def minusFile(file: FilePlus): File = file.file
}