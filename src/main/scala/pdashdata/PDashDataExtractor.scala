package pdashdata

import java.io.File
import java.io.FileInputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

import java.util.List
import java.util.Map
import java.util.HashMap

import scala.jdk.CollectionConverters._

class PDashDataExtractor {
    def extract(path: String): ProcessData = {        
        var timeLogs: List[TimeLog] = null
        var defectLogs: Map[Int, List[DefectLog]] = new HashMap[Int, List[DefectLog]]()
        var programDatas: Map[Int, Map[String, Any]] = new HashMap[Int, Map[String, Any]]()

        try {
            val zipFile = new File(path)
            val zipInputStream = new ZipInputStream(new FileInputStream(zipFile))
            
            var zipEntry: ZipEntry = zipInputStream.getNextEntry()
            while (zipEntry != null) {
                val input = ParserUtils.zipInputToByteArrayInput(zipInputStream)
                zipEntry.getName() match {
                    case "timelog.xml" => 
                        timeLogs = LogFileParser.parseTimeLog(input).asJava
                    case "1.dat" =>
                        programDatas.put(1, DataFileParser.parseDataFile(input).asJava)
                    case "2.dat" =>
                        programDatas.put(2, DataFileParser.parseDataFile(input).asJava)
                    case "3.dat" =>
                        programDatas.put(3, DataFileParser.parseDataFile(input).asJava)
                    case "4.dat" =>
                        programDatas.put(4, DataFileParser.parseDataFile(input).asJava)
                    case "6.dat" =>
                        programDatas.put(5, DataFileParser.parseDataFile(input).asJava)
                    case "7.dat" =>
                        programDatas.put(6, DataFileParser.parseDataFile(input).asJava)
                    case "8.dat" =>
                        programDatas.put(7, DataFileParser.parseDataFile(input).asJava)
                    case "9.dat" =>
                        programDatas.put(8, DataFileParser.parseDataFile(input).asJava)
                    case "0.def" =>
                        defectLogs.put(1, LogFileParser.parseDefectLog(input).asJava)
                    case "1.def" =>
                        defectLogs.put(2, LogFileParser.parseDefectLog(input).asJava)
                    case "2.def" =>
                        defectLogs.put(3, LogFileParser.parseDefectLog(input).asJava)
                    case "3.def" =>
                        defectLogs.put(4, LogFileParser.parseDefectLog(input).asJava)
                    case "5.def" =>
                        defectLogs.put(5, LogFileParser.parseDefectLog(input).asJava)
                    case "6.def" =>
                        defectLogs.put(6, LogFileParser.parseDefectLog(input).asJava)
                    case "7.def" =>
                        defectLogs.put(7, LogFileParser.parseDefectLog(input).asJava)
                    case "8.def" =>
                        defectLogs.put(8, LogFileParser.parseDefectLog(input).asJava)
                    case _ => null
                }
                zipEntry = zipInputStream.getNextEntry()
            }
        } catch {
            case e: Exception => println(e)
        }

        new ProcessData(timeLogs, defectLogs, programDatas)
    }
}
