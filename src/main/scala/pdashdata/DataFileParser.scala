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

        val key = parts(0)

        // "=="で区切られている場合はparts.lengthが3となるので、parts(2)から取得
        // そうでない場合はparts.lengthが2となるので、parts(1)から取得
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

    /**
     * 文字列を適切な型に変換する
     */
    private def convertValue(value: String): Any = {
        value match {
            case strDate if strDate.startsWith("@")
                // @で始まる文字列はUNIX時間(ミリ秒)
                => new Date(strDate.substring(1).toLong)
            case strList if strList.charAt(0) == '\u0002'
                // 制御文字STRで始まる場合はリストに変換
                // 先頭だけ余るのでtailで取り除く
                => strList.split("\u0002").toList.tail
            case strNumber if isDouble(strNumber)
                => strNumber.toDouble
            case strNaN if strNaN == "N/A"
                => Double.NaN
            case strNull if strNull == "null"
                => Double.NaN
            case _
                => value
        }
    }
    
    /**
     * 文字列がDoubleに変換可能かどうかを判定する
     */
    private def isDouble(value: String): Boolean = {
        try {
            value.toDouble
            true
        } catch {
            case _: Throwable => false
        }
    }
}
