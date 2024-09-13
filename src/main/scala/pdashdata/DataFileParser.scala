import java.io.ByteArrayOutputStream
import java.util.zip.ZipInputStream

import java.util.Date

class DataFileParser {
    def parseLine(line: String): (String, String) = {
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

    def parseDataFile(zipInputStream: ZipInputStream): Map[String, Any] = {
        val buffer = new ByteArrayOutputStream()
        val data = new Array[Byte](1024)

        var count = zipInputStream.read(data)
        while (count != -1) {
            buffer.write(data, 0, count)
            count = zipInputStream.read(data)
        }

        val lines = buffer.toString()
                          .split("\\r?\\n")
                          .toList
                          .slice(2, buffer.size() - 1)

        var dataMap = Map.empty[String, Any]
        lines.foreach((line) => {
            val (key, strValue) = parseLine(line)

            val value: Any = strValue match {
                case strDate if strDate.startsWith("@")
                    => new Date(strDate.substring(1).toLong)
                case strList if strList.charAt(0) == '\u0002'
                    // 制御文字STRで始まる場合はリストに変換
                    // 先頭だけ余るのでtailで取り除く
                    => strList.split("\u0002").toList.tail
                case strNumber if isDouble(strNumber)
                    => strNumber.toDouble
                case _
                    => strValue
            }

            dataMap = dataMap + (key -> value)
        })

        dataMap
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
