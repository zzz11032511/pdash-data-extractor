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
            return null
        }

        val a = loadBaseParts(programDatas(4))
        a.foreach(println)

        val b = loadAdditionalParts(programDatas(4))
        b.foreach(println)

        val c = loadReusedParts(programDatas(6))
        c.foreach(println)

        new ProcessData(timeLogs, defectLogs, programDatas)
    }

    private def loadBaseParts(programData: Map[String, Any]): List[BaseParts] = {
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

    private def loadAdditionalParts(programData: Map[String, Any]): List[AdditionalParts] = {
        var additionalParts = List[AdditionalParts]()

        programData.get("New_Objects_List")
                   .getOrElse(List.empty[String])
                   .asInstanceOf[List[String]]
                   .foreach((programNum: String) => {
            val name = getStringValue(programData, s"New Objects/$programNum/Description")
            val estSize = getDoubleValue(programData, s"New Objects/$programNum/LOC")
            val actSize = getDoubleValue(programData, s"New Objects/$programNum/Actual LOC")
            val relativeSize = getStringValue(programData, s"New Objects/$programNum/Relative Size")
            val estNumOfMethod = getIntValue(programData, s"New Objects/$programNum/Methods")
            val estIsNewReused = getDoubleValue(programData, s"New Objects/$programNum/New Reused?") == 1.0
            val actNumOfMethod = getIntValue(programData, s"New Objects/$programNum/Actual Methods")
            val actIsNewReused = getDoubleValue(programData, s"New Objects/$programNum/Actual New Reused?") == 1.0
            val methodType = getStringValue(programData, s"New Objects/$programNum/Type")

            val additionalPart = new AdditionalParts(
                name, estSize, actSize, relativeSize,
                estNumOfMethod, estIsNewReused,
                actNumOfMethod, actIsNewReused,
                methodType)

            additionalParts = additionalParts :+ additionalPart
        })

        additionalParts
    }

    private def loadReusedParts(programData: Map[String, Any]): List[ReusedParts] = {
        var reusedParts = List[ReusedParts]()

        programData.get("Reused_Objects_List")
                   .getOrElse(List.empty[String])
                   .asInstanceOf[List[String]]
                   .foreach((programNum: String) => {
            val name = getStringValue(programData, s"Reused Objects/$programNum/Description")
            val estSize = getDoubleValue(programData, s"Reused Objects/$programNum/LOC")
            val actSize = getDoubleValue(programData, s"Reused Objects/$programNum/Actual LOC")

            val reusedPart = new ReusedParts(name, estSize, actSize)

            reusedParts = reusedParts :+ reusedPart
        })

        reusedParts
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
}
