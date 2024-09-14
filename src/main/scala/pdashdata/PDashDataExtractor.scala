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
import org.w3c.dom.NamedNodeMap

import java.util.Date
import java.util.jar.Attributes.Name
import java.io.InputStream

object PDashDataExtractorMain {
  def main(args: Array[String]): Unit = {

    val pData = new ProcessData(
      List.empty[TimeLog],
      Map.empty[Int, List[DefectLog]],
      Map.empty[Int, Map[String, Any]]
    )

    val defParser = new DataFileParser()

    try {
      val zipFile = new File("./pdash-Furuta_Naoki-2024-07-27.zip")
      val zipInputStream = new ZipInputStream(new FileInputStream(zipFile))
      
      
      var zipEntry: ZipEntry = zipInputStream.getNextEntry()
      while (zipEntry != null) {
        // println("file : " + zipEntry.getName())

        val input = ParserUtils.zipInputStreamToByteArrayInputStream(zipInputStream)
        zipEntry.getName() match {
          case "timelog.xml" => 
            pData.timeLog = loadTimeLog(input)
          case "1.dat" =>
            pData.programDatas = pData.programDatas + (1 -> defParser.parseDataFile(input))
          case "2.dat" =>
            pData.programDatas = pData.programDatas + (2 -> defParser.parseDataFile(input))
          case "3.dat" =>
            pData.programDatas = pData.programDatas + (3 -> defParser.parseDataFile(input))
          case "4.dat" =>
            pData.programDatas = pData.programDatas + (4 -> defParser.parseDataFile(input))
          case "6.dat" =>
            pData.programDatas = pData.programDatas + (5 -> defParser.parseDataFile(input))
          case "7.dat" =>
            pData.programDatas = pData.programDatas + (6 -> defParser.parseDataFile(input))
          case "8.dat" =>
            pData.programDatas = pData.programDatas + (7 -> defParser.parseDataFile(input))
          case "9.dat" =>
            pData.programDatas = pData.programDatas + (8 -> defParser.parseDataFile(input))
          case "0.def" =>
            pData.defectLogs = pData.defectLogs + (1 -> loadDefectLog(input))
          case "1.def" =>
            pData.defectLogs = pData.defectLogs + (2 -> loadDefectLog(input))
          case "2.def" =>
            pData.defectLogs = pData.defectLogs + (3 -> loadDefectLog(input))
          case "3.def" =>
            pData.defectLogs = pData.defectLogs + (4 -> loadDefectLog(input))
          case "5.def" =>
            pData.defectLogs = pData.defectLogs + (5 -> loadDefectLog(input))
          case "6.def" =>
            pData.defectLogs = pData.defectLogs + (6 -> loadDefectLog(input))
          case "7.def" =>
            pData.defectLogs = pData.defectLogs + (7 -> loadDefectLog(input))
          case "8.def" =>
            pData.defectLogs = pData.defectLogs + (8 -> loadDefectLog(input))
          case _ => null
        }
        
        zipEntry = zipInputStream.getNextEntry()
      }
    } catch {
      case e: Exception => println(e)
    }
  }

  private def loadTimeLog(input: InputStream): List[TimeLog] = {
    val factory = DocumentBuilderFactory.newInstance()
    val builder = factory.newDocumentBuilder()
    val doc = builder.parse(input)
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

  private def loadDefectLog(input: InputStream): List[DefectLog] = {
    val factory = DocumentBuilderFactory.newInstance()
    val builder = factory.newDocumentBuilder()
    val doc = builder.parse(input)
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
