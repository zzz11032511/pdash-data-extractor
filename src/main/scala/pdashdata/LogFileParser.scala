package pdashdata

import java.io.InputStream
import javax.xml.parsers.DocumentBuilderFactory
import org.w3c.dom.Node
import org.w3c.dom.Element

object LogFileParser {
    /**
     * pdashのTimeLog.xmlをパースする
     * 
     * @param input TimeLog.xmlを読み込んだInputStream
     * @return TimeLogのリスト
     */
    private[pdashdata] def parseTimeLog(input: InputStream): List[TimeLog] = {
        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()

        try {
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
        } catch {
            case parserError: org.xml.sax.SAXParseException => List.empty[TimeLog]
        }
    }

    private def xmlElementToTimeLog(element: Element): TimeLog = {
        val path = safeGetAttributes(element, "path").getOrElse("")
        new TimeLog(
            id = safeGetAttributes(element, "id").getOrElse("0").toInt,
            program = pathToProgram(path),
            phase = pathToPhase(path),
            startTime = ParserUtils.timeStrToDate(safeGetAttributes(element, "start").getOrElse("0")),
            delta = safeGetAttributes(element, "delta").getOrElse("0").toDouble,
            interrupt = safeGetAttributes(element, "interrupt").getOrElse("0").toDouble,
            comment = safeGetAttributes(element, "comment").getOrElse("comment")
        )
    }

    /**
     * pdashのDefectLog.xmlをパースする
     * 
     * @param input DefectLog.xmlを読み込んだInputStream
     * @return DefectLogのリスト
     */
    private[pdashdata] def parseDefectLog(input: InputStream): List[DefectLog] = {
        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()

        try {
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
        } catch {
            case parserError: org.xml.sax.SAXParseException => List.empty[DefectLog]
        }
    }

    private def xmlElementToDefectLog(element: Element): DefectLog = {
        val path = safeGetAttributes(element, "path").getOrElse("")
        new DefectLog(
            id = safeGetAttributes(element, "num").getOrElse("0").toInt,
            count = safeGetAttributes(element, "count").getOrElse("1").toInt,
            defectType = safeGetAttributes(element, "type").getOrElse(""),
            injected = safeGetAttributes(element, "inj").getOrElse(""),
            removed = safeGetAttributes(element, "rem").getOrElse(""),
            fixTime = safeGetAttributes(element, "ft").getOrElse("0").toDouble,
            fixDefectID = safeGetAttributes(element, "fd").getOrElse("0").toInt,
            description = safeGetAttributes(element, "desc").getOrElse(""),
            injectedDate = ParserUtils.timeStrToDate(safeGetAttributes(element, "date").getOrElse("0"))
        )
    }


    private def safeGetAttributes(element: Element, name: String): Option[String] = {
        val value = element.getAttribute(name)
        if (value.isEmpty) {
            None
        } else {
            Some(value)
        }
    }

    private def pathToProgram(path: String): String = {
        val parts = path.split("/")
        if (parts.length < 4) "" else parts(3)
    }

    private def pathToPhase(path: String): String = {
        val parts = path.split("/")
        if (parts.length < 5) "" else parts(4)
    }
}
