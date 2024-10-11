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
                        // logs.itemにはElement以外のノードも含まれるが、
                        // データが格納されているのはElementノードのみ
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

    /**
     * XML ElementをTimeLogオブジェクトに変換する
     */
    private def xmlElementToTimeLog(element: Element): TimeLog = {
        val path = safeGetAttributes(element, "path").getOrElse("")

        // レポート課題の時間ログの場合はプログラム番号を0とする
        val programNumber = if (path.contains("Report")) then 0 else pathToProgramNumber(path)

        new TimeLog(
            id = safeGetAttributes(element, "id").getOrElse("0").toInt,
            programNumber = programNumber,
            phase = pathToPhase(path),
            startTime = ParserUtils.timeStrToDate(safeGetAttributes(element, "start").getOrElse("0")),
            delta = safeGetAttributes(element, "delta").getOrElse("0").toDouble,
            interrupt = safeGetAttributes(element, "interrupt").getOrElse("0").toDouble,
            comment = safeGetAttributes(element, "comment").getOrElse("comment")
        )
    }

    /**
     * パスからフェーズ名を取得する
     * 
     * TimeLogには "Non Project/PSP for Engineers/Program 1/フェーズ名"
     * という形式の文字列(path)が格納されている
     */
    private def pathToPhase(path: String): String = {
        val parts = path.split("/")
        if (parts.length < 5) "" else parts(4)
    }

    /**
     * pdashのDefectLog.xmlをパースする
     * 
     * DefectLogにはプログラム番号が含まれていないため、
     * 引数で現在読み込んでいるDefectLogファイルのプログラム番号を指定する
     * 
     * @param input DefectLog.xmlを読み込んだInputStream
     * @param programNumber プログラム番号
     * @return DefectLogのリスト
     */
    private[pdashdata] def parseDefectLog(input: InputStream, programNumber: Int): List[DefectLog] = {
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
                        // logs.itemにはElement以外のノードも含まれるが、
                        // データが格納されているのはElementノードのみ
                        val defectLog = xmlElementToDefectLog(logs.item(i).asInstanceOf[Element], programNumber)
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

    /**
     * XML ElementをDefectLogオブジェクトに変換する
     */
    private def xmlElementToDefectLog(element: Element, programNumber: Int): DefectLog = {
        val path = safeGetAttributes(element, "path").getOrElse("")
        new DefectLog(
            id = safeGetAttributes(element, "num").getOrElse("0").toInt,
            programNumber = programNumber,
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


    /**
     * XML Element(Logデータ)から属性値を安全に取得する
     * 
     * 属性値が存在する場合はSome(value)、
     * 属性値が空文字の場合はNoneを返す
     */
    private def safeGetAttributes(element: Element, name: String): Option[String] = {
        val value = element.getAttribute(name)
        if (value.isEmpty) {
            None
        } else {
            Some(value)
        }

    }

    /**
     * パスからプログラム番号をInt型で取得する
     * 
     * TimeLogには "Non Project/PSP for Engineers/Program 課題番号/フェーズ名"
     * という形式の文字列(path)が格納されている
     */
    private def pathToProgramNumber(path: String): Int = {
        val parts = path.split("/")
        if (parts.length < 4) {
            0
        } else {
            // 例: "Program 1" -> ["Program", "1"](1) -> 1
            parts(3).split(" ")(1).toInt
        }
    }
}
