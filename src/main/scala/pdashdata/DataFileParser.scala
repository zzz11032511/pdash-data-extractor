package pdashdata

import java.io.InputStream
import java.util.Date

object DataFileParser {
    /**
      * pdashのデータファイルの1行をパースする
      * "key=value"の形式で格納されているデータを(key, value)のタプルに変換する
      *
      * @param line データファイルから読み込んだ1行
      * @return (key, value)
      */
    private[pdashdata] def parseLine(line: String): (String, String) = {
        val parts = line.split("=")

        var key = parts(0)
        // parts.length == 3は"=="で区切られている場合を表す
        var value = if (parts.length == 3) parts(2) else parts(1)

        // "=\"で区切られている場合は、valueの先頭の"を削除
        if (value.startsWith("\"")) {
            value = value.substring(1)
        }

        (key, value)
    }

    /**
     * pdashのデータファイルをパースする
     * 
     * @param input データファイルを読み込んだInputStream
     * @return Map(key -> value)
     * @note valueの型はAnyだが、データの種類によって以下の型に変換可能
     *       - Date : 日付
     *       - List[String] : PROBEに使用した課題のリストなどが該当
     *       - Double : 数値
     *       - String : Additional Partsの名前などが該当
     */
    private[pdashdata] def parseDataFile(input: InputStream): Map[String, Any] = {
        val lines = scala.io.Source.fromInputStream(input).getLines()

        // 最初の2行はヘッダーなので読み飛ばす
        // データが無い場合は例外が発生するので空のMapを返す
        try {
            lines.next()
            lines.next()
        } catch {
            case _: NoSuchElementException => return Map.empty
        }

        var programData = Map.empty[String, Any]
        for (line <- lines) {
            val (key, value) = parseLine(line)
            programData = programData + (key -> convertValue(value))
        }

        programData
    }

    private[pdashdata] def loadBaseParts(programData: Map[String, Any]): List[BaseParts] = {
        var baseParts = List[BaseParts]()

        programData.get("Base_Parts_List")
                   .getOrElse(List.empty[String])
                   .asInstanceOf[List[String]]
                   .foreach((programNum: String) => {
            val name = getStringValue(programData, s"Base_Parts/$programNum/Description")
            val estBase = getDoubleValue(programData, s"Base_Parts/$programNum/Base")
            val estAdded = getDoubleValue(programData, s"Base_Parts/$programNum/Added")
            val estModified = getDoubleValue(programData, s"Base_Parts/$programNum/Modified")
            val estDeleted = getDoubleValue(programData, s"Base_Parts/$programNum/Deleted")
            val actBase = getDoubleValue(programData, s"Base_Parts/$programNum/Actual Base")
            val actAdded = getDoubleValue(programData, s"Base_Parts/$programNum/Actual Added")
            val actModified = getDoubleValue(programData, s"Base_Parts/$programNum/Actual Modified")
            val actDeleted = getDoubleValue(programData, s"Base_Parts/$programNum/Actual Deleted")

            val basePart = new BaseParts(
                name, estBase, estAdded, estModified, estDeleted,
                actBase, actAdded, actModified, actDeleted
            )

            baseParts = baseParts :+ basePart
        })

        baseParts
    }

    private def getStringValue(programData: Map[String, Any], key: String): String = {
        programData.get(key).getOrElse("").asInstanceOf[String]
    }

    private def getDoubleValue(programData: Map[String, Any], key: String): Double = {
        programData.get(key).getOrElse(0.0).asInstanceOf[Double]
    }

    private def getIntValue(programData: Map[String, Any], key: String): Int = {
        getDoubleValue(programData, key).toInt
    }

    private def convertValue(value: String): Any = {
        value match {
            case strDate if strDate.startsWith("@")
                => new Date(strDate.substring(1).toLong)
            case strList if strList.charAt(0) == '\u0002'
                // 制御文字STRで始まる場合はリストに変換
                // 先頭だけ余るのでtailで取り除く
                => strList.split("\u0002").toList.tail
            case strNumber if isDouble(strNumber)
                => strNumber.toDouble
            case _
                => value
        }
    }
    
    private def isDouble(value: String): Boolean = {
        try {
            value.toDouble
            true
        } catch {
            case _: Throwable => false
        }
    }
}
