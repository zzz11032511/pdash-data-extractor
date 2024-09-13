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
}
