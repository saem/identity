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
    catalogueRepository.save(catalogue)
    redirect("/catalogues/" + catalogue.name)
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

class CatalogueRepository(dao: CatalogueDao) {
  def findAll(): Option[Seq[Catalogue]] = {
    dao.loadAll()
  }

  def save(cat: Catalogue) {
    dao.save(cat)
  }
}

trait CatalogueDao {
  def loadAll(): Option[Seq[Catalogue]]
  def save(catalogue: Catalogue): Option[Catalogue]

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