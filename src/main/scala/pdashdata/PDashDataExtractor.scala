import java.io.File
import java.io.FileInputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory

import org.w3c.dom.Document
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import org.w3c.dom.Element
import org.w3c.dom.Attr

import java.util.Date
import org.w3c.dom.NamedNodeMap
import java.util.jar.Attributes.Name

object PDashDataExtractorMain {
  def main(args: Array[String]): Unit = {

    try {
      val zipFile = new File("./pdash-Furuta_Naoki-2024-07-27.zip")
      val zipInputStream = new ZipInputStream(new FileInputStream(zipFile))
      
      var zipEntry: ZipEntry = zipInputStream.getNextEntry()
      while (zipEntry != null) {
        printf("file : ")
        println(zipEntry.getName())
        zipEntry.getName() match {
          case "timelog.xml" => {
            val timeLogs = loadTimeLog(zipInputStream)
          }
          case name if name.endsWith(".def") => {
            val defectLogs = loadDefectLog(zipInputStream)
          }
          case _ => null
        }
        
        zipEntry = zipInputStream.getNextEntry()
      }
    } catch {
      case e: Exception => println(e)
    }
  }

  private def loadTimeLog(zipInputStream: ZipInputStream): List[TimeLog] = {
    val byteArray = readZipEntryToByteArray(zipInputStream)
    val byteArrayInputStream = new ByteArrayInputStream(byteArray)

    val factory = DocumentBuilderFactory.newInstance()
    val builder = factory.newDocumentBuilder()
    val doc = builder.parse(byteArrayInputStream)
    val root = doc.getDocumentElement()

    var timeLogs: List[TimeLog] = Nil

    val logs = root.getChildNodes()
    for (i <- 0 until logs.getLength()) {
      logs.item(i).getNodeType() match {
        case Node.ELEMENT_NODE => {
          val timeLog = xmlElementToTimeLog(logs.item(i).asInstanceOf[Element])
          timeLogs = timeLogs :+ timeLog
        }
        case _ => null
      }
    }

    timeLogs
  }

  private def xmlElementToTimeLog(element: Element): TimeLog = {
    val path = safeGetAttributes(element, "path").getOrElse("")
    new TimeLog(
      ID = safeGetAttributes(element, "id").getOrElse("0").toInt,
      program = pathToProgram(path),
      phase = pathToPhase(path),
      startTime = timeStrToDate(safeGetAttributes(element, "start").getOrElse("0")),
      delta = safeGetAttributes(element, "delta").getOrElse("0").toDouble,
      interrupt = safeGetAttributes(element, "interrupt").getOrElse("0").toDouble,
      comment = safeGetAttributes(element, "comment").getOrElse("comment")
    )
  }

  private def loadDefectLog(zipInputStream: ZipInputStream): List[DefectLog] = {
    val byteArray = readZipEntryToByteArray(zipInputStream)
    val byteArrayInputStream = new ByteArrayInputStream(byteArray)

    val factory = DocumentBuilderFactory.newInstance()
    val builder = factory.newDocumentBuilder()
    val doc = builder.parse(byteArrayInputStream)
    val root = doc.getDocumentElement()

    var defectLogs: List[DefectLog] = Nil

    val logs = root.getChildNodes()
    for (i <- 0 until logs.getLength()) {
      logs.item(i).getNodeType() match {
        case Node.ELEMENT_NODE => {
          val defectLog = xmlElementToDefectLog(logs.item(i).asInstanceOf[Element])
          defectLogs = defectLogs :+ defectLog
        }
        case _ => null
      }
    }

    defectLogs
  }

  private def xmlElementToDefectLog(element: Element): DefectLog = {
    val path = safeGetAttributes(element, "path").getOrElse("")
    new DefectLog(
      ID = safeGetAttributes(element, "num").getOrElse("0").toInt,
      defectType = safeGetAttributes(element, "type").getOrElse(""),
      injected = safeGetAttributes(element, "inj").getOrElse(""),
      removed = safeGetAttributes(element, "rem").getOrElse(""),
      fixTime = safeGetAttributes(element, "ft").getOrElse("0").toDouble,
      fixDefectID = safeGetAttributes(element, "fd").getOrElse("0").toInt,
      description = safeGetAttributes(element, "desc").getOrElse(""),
      injectedDate = timeStrToDate(safeGetAttributes(element, "date").getOrElse("0"))
    )
  }


  private def safeGetAttributes(element: Element, name: String): Option[String] = {
    val value = element.getAttribute(name)
    if (value.isEmpty()) {
      None
    } else {
      Some(value)
    }
  }

  private def timeStrToDate(timeStr: String): Date = {
    if (timeStr.charAt(0) == '@') {
      return new Date(timeStr.substring(1).toLong)
    }
    new Date(timeStr.toLong)
  }

  private def readZipEntryToByteArray(zipInputStream: ZipInputStream): Array[Byte] = {
    val buffer = new ByteArrayOutputStream()
    val data = new Array[Byte](1024)

    var count = zipInputStream.read(data)
    while (count != -1) {
      buffer.write(data, 0, count)
      count = zipInputStream.read(data)
    }
    
    buffer.toByteArray
  }

  private def pathToProgram(path: String): String = {
    val parts = path.split("/")
    
    if (parts.length < 4) {
      return ""
    }
    parts(3)
  }

  private def pathToPhase(path: String): String = {
    val parts = path.split("/")
    
    if (parts.length < 5) {
      return ""
    }
    parts(4)
  }
}
