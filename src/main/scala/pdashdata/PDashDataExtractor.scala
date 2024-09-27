package pdashdata

import java.io.File
import java.io.FileInputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

class PDashDataExtractor {
    def extract(path: String): ProcessData = {        
        var timeLogs: List[TimeLog] = null
        var defectLogs: Map[Int, List[DefectLog]] = Map[Int, List[DefectLog]]()
        var programDatas: Map[Int, Map[String, Any]] = Map[Int, Map[String, Any]]()

        try {
            val zipFile = new File(path)
            val zipInputStream = new ZipInputStream(new FileInputStream(zipFile))
            
            var zipEntry: ZipEntry = zipInputStream.getNextEntry()
            while (zipEntry != null) {
                val input = ParserUtils.zipInputToByteArrayInput(zipInputStream)
                zipEntry.getName() match {
                    case "timelog.xml" => 
                        timeLogs = LogFileParser.parseTimeLog(input)
                    case "1.dat" =>
                        val pdataMap = Map(1 -> DataFileParser.parseDataFile(input))
                        programDatas = programDatas ++ pdataMap
                    case "2.dat" =>
                        val pdataMap = Map(2 -> DataFileParser.parseDataFile(input))
                        programDatas = programDatas ++ pdataMap
                    case "3.dat" =>
                        val pdataMap = Map(3 -> DataFileParser.parseDataFile(input))
                        programDatas = programDatas ++ pdataMap
                    case "4.dat" =>
                        val pdataMap = Map(4 -> DataFileParser.parseDataFile(input))
                        programDatas = programDatas ++ pdataMap
                    case "6.dat" =>
                        val pdataMap = Map(5 -> DataFileParser.parseDataFile(input))
                        programDatas = programDatas ++ pdataMap
                    case "7.dat" =>
                        val pdataMap = Map(6 -> DataFileParser.parseDataFile(input))
                        programDatas = programDatas ++ pdataMap
                    case "8.dat" =>
                        val pdataMap = Map(7 -> DataFileParser.parseDataFile(input))
                        programDatas = programDatas ++ pdataMap
                    case "9.dat" =>
                        val pdataMap = Map(8 -> DataFileParser.parseDataFile(input))
                        programDatas = programDatas ++ pdataMap
                    case "0.def" =>
                        val dLogMap = Map(1 -> LogFileParser.parseDefectLog(input))
                        defectLogs = defectLogs ++ dLogMap
                    case "1.def" =>
                        val dLogMap = Map(2 -> LogFileParser.parseDefectLog(input))
                        defectLogs = defectLogs ++ dLogMap
                    case "2.def" =>
                        val dLogMap = Map(3 -> LogFileParser.parseDefectLog(input))
                        defectLogs = defectLogs ++ dLogMap
                    case "3.def" =>
                        val dLogMap = Map(4 -> LogFileParser.parseDefectLog(input))
                        defectLogs = defectLogs ++ dLogMap
                    case "5.def" =>
                        val dLogMap = Map(5 -> LogFileParser.parseDefectLog(input))
                        defectLogs = defectLogs ++ dLogMap
                    case "6.def" =>
                        val dLogMap = Map(6 -> LogFileParser.parseDefectLog(input))
                        defectLogs = defectLogs ++ dLogMap
                    case "7.def" =>
                        val dLogMap = Map(7 -> LogFileParser.parseDefectLog(input))
                        defectLogs = defectLogs ++ dLogMap
                    case "8.def" =>
                        val dLogMap = Map(8 -> LogFileParser.parseDefectLog(input))
                        defectLogs = defectLogs ++ dLogMap
                    case _ => null
                }
                zipEntry = zipInputStream.getNextEntry()
            }
        } catch {
            case e: Exception => println(e)
        }

        val a = DataFileParser.loadBaseParts(programDatas(4))
        a.foreach(println)

        new ProcessData(timeLogs, defectLogs, programDatas)
    }
}
