package pdashdata

import java.io.File
import java.io.FileInputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.Date

object PDashDataExtractor {
    /**
      * Process Dashboardのプロセスデータを抽出する
      *
      * @param path プロセスデータのZIPファイルのパス
      * @return プロセスデータ
      */
    def extract(path: String): ProcessData = {        
        var timeLogs: List[TimeLog] = null
        var defectLogMap: Map[Int, List[DefectLog]] = Map[Int, List[DefectLog]]()
        var dataFileMap: Map[Int, Map[String, Any]] = Map[Int, Map[String, Any]]()

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
                        dataFileMap = dataFileMap ++ pdataMap
                    case "2.dat" =>
                        val pdataMap = Map(2 -> DataFileParser.parseDataFile(input))
                        dataFileMap = dataFileMap ++ pdataMap
                    case "3.dat" =>
                        val pdataMap = Map(3 -> DataFileParser.parseDataFile(input))
                        dataFileMap = dataFileMap ++ pdataMap
                    case "4.dat" =>
                        val pdataMap = Map(4 -> DataFileParser.parseDataFile(input))
                        dataFileMap = dataFileMap ++ pdataMap
                    case "6.dat" =>
                        val pdataMap = Map(5 -> DataFileParser.parseDataFile(input))
                        dataFileMap = dataFileMap ++ pdataMap
                    case "7.dat" =>
                        val pdataMap = Map(6 -> DataFileParser.parseDataFile(input))
                        dataFileMap = dataFileMap ++ pdataMap
                    case "8.dat" =>
                        val pdataMap = Map(7 -> DataFileParser.parseDataFile(input))
                        dataFileMap = dataFileMap ++ pdataMap
                    case "9.dat" =>
                        val pdataMap = Map(8 -> DataFileParser.parseDataFile(input))
                        dataFileMap = dataFileMap ++ pdataMap
                    case "0.def" =>
                        val dLogMap = Map(1 -> LogFileParser.parseDefectLog(input))
                        defectLogMap = defectLogMap ++ dLogMap
                    case "1.def" =>
                        val dLogMap = Map(2 -> LogFileParser.parseDefectLog(input))
                        defectLogMap = defectLogMap ++ dLogMap
                    case "2.def" =>
                        val dLogMap = Map(3 -> LogFileParser.parseDefectLog(input))
                        defectLogMap = defectLogMap ++ dLogMap
                    case "3.def" =>
                        val dLogMap = Map(4 -> LogFileParser.parseDefectLog(input))
                        defectLogMap = defectLogMap ++ dLogMap
                    case "5.def" =>
                        val dLogMap = Map(5 -> LogFileParser.parseDefectLog(input))
                        defectLogMap = defectLogMap ++ dLogMap
                    case "6.def" =>
                        val dLogMap = Map(6 -> LogFileParser.parseDefectLog(input))
                        defectLogMap = defectLogMap ++ dLogMap
                    case "7.def" =>
                        val dLogMap = Map(7 -> LogFileParser.parseDefectLog(input))
                        defectLogMap = defectLogMap ++ dLogMap
                    case "8.def" =>
                        val dLogMap = Map(8 -> LogFileParser.parseDefectLog(input))
                        defectLogMap = defectLogMap ++ dLogMap
                    case _ => null
                }
                zipEntry = zipInputStream.getNextEntry()
            }
        } catch {
            case e: Exception => println(e)
            return null
        }

        new ProcessData(
            timeLogs,
            flatDefectLogMap(defectLogMap),
            loadProgramDatas(timeLogs, defectLogMap, dataFileMap)
        )
    }

    private def flatDefectLogMap(defectLogMap: Map[Int, List[DefectLog]]): List[DefectLog] = {
        defectLogMap.get(1).getOrElse(List.empty[DefectLog]) ++
        defectLogMap.get(2).getOrElse(List.empty[DefectLog]) ++
        defectLogMap.get(3).getOrElse(List.empty[DefectLog]) ++
        defectLogMap.get(4).getOrElse(List.empty[DefectLog]) ++
        defectLogMap.get(5).getOrElse(List.empty[DefectLog]) ++
        defectLogMap.get(6).getOrElse(List.empty[DefectLog]) ++
        defectLogMap.get(7).getOrElse(List.empty[DefectLog]) ++
        defectLogMap.get(8).getOrElse(List.empty[DefectLog])
    }

    private def loadProgramDatas(timeLogs: List[TimeLog], defectLogMap: Map[Int, List[DefectLog]], dataFileMaps: Map[Int, Map[String, Any]]): Map[Int, ProgramData] = {
        var programDatas = Map[Int, ProgramData]()

        dataFileMaps.foreach((num, dataFileMap) => {
            val basePart = loadBaseParts(dataFileMap)
            val additionalPart = loadAdditionalParts(dataFileMap)
            val reusedPart = loadReusedParts(dataFileMap)
            val sizeEstimateData = loadSizeEstimateData(dataFileMap, "Estimated New & Changed LOC")
            val timeEstimateData = loadSizeEstimateData(dataFileMap, "Estimated Time")
            val probeList = dataFileMap.get("PROBE_LIST").getOrElse(List.empty[String]).asInstanceOf[List[String]]
            val totalSize = getIntValue(dataFileMap, "Total LOC")

            val process = num match {
                case 1 => "PSP0"
                case 2 => "PSP0.1"
                case 3 => "PSP1.0"
                case 4 => "PSP1.1"
                case 5 => "PSP2.0"
                case _ => "PSP2.1"
            }

            val programData = new ProgramData(
                num, process, timeLogs.filter(_.program == s"Program $num"), 
                defectLogMap.get(num).getOrElse(List.empty[DefectLog]),
                loadPhaseDatas(num, timeLogs, defectLogMap, dataFileMap),
                basePart, additionalPart, reusedPart,
                sizeEstimateData, timeEstimateData, probeList, totalSize
            )

            programDatas = programDatas + (num -> programData)
        })

        programDatas
    }

    private def loadPhaseDatas(num: Int, timeLogs: List[TimeLog], defectLogMap: Map[Int, List[DefectLog]], dataFileMap: Map[String, Any]): Map[String, PhaseData] = {
        var phaseDatas = Map[String, PhaseData]()

        phaseDatas = phaseDatas
            + ("Planning" -> loadPhaseData(num, "Planning", timeLogs, defectLogMap, dataFileMap))
        phaseDatas = phaseDatas
            + ("Design" -> loadPhaseData(num, "Design", timeLogs, defectLogMap, dataFileMap))
        phaseDatas = phaseDatas
            + ("Design Review" -> loadPhaseData(num, "Design Review", timeLogs, defectLogMap, dataFileMap))
        phaseDatas = phaseDatas
            + ("Code" -> loadPhaseData(num, "Code", timeLogs, defectLogMap, dataFileMap))
        phaseDatas = phaseDatas
            + ("Code Review" -> loadPhaseData(num, "Code Review", timeLogs, defectLogMap, dataFileMap))
        phaseDatas = phaseDatas
            + ("Compile" -> loadPhaseData(num, "Compile", timeLogs, defectLogMap, dataFileMap))
        phaseDatas = phaseDatas
            + ("Test" -> loadPhaseData(num, "Test", timeLogs, defectLogMap, dataFileMap))
        phaseDatas = phaseDatas
            + ("Postmortem" -> loadPhaseData(num, "Postmortem", timeLogs, defectLogMap, dataFileMap))

        phaseDatas
    }

    private def loadPhaseData(num: Int, phase: String, timeLogs: List[TimeLog], defectLogMap: Map[Int, List[DefectLog]], dataFileMap: Map[String, Any]): PhaseData = {
        val started = getDateValue(dataFileMap, s"$phase/Started")
        val completed = getDateValue(dataFileMap, s"$phase/Completed")
        val time = getDoubleValue(dataFileMap, s"$phase/Time")
        val estTime = getDoubleValue(dataFileMap, s"$phase/Estimated Time")
        val injectDefects = getDoubleValue(dataFileMap, s"$phase/Defects Injected")
        val estInjectDefects = getDoubleValue(dataFileMap, s"$phase/Estimated Defects Injected")
        val removeDefects = getDoubleValue(dataFileMap, s"$phase/Defects Removed")
        val estRemoveDefects = getDoubleValue(dataFileMap, s"$phase/Estimated Defects Removed")

        new PhaseData(
            started, completed, time, estTime, 
            injectDefects, estInjectDefects, removeDefects, estRemoveDefects
        )
    }

    private def loadBaseParts(programData: Map[String, Any]): List[BasePart] = {
        var baseParts = List[BasePart]()

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

            val basePart = new BasePart(
                name, estBase, estAdded, estModified, estDeleted,
                actBase, actAdded, actModified, actDeleted
            )

            // 名前が空の場合は追加しない
            if (name != "") {
                baseParts = baseParts :+ basePart
            }
        })

        baseParts
    }

    private def loadAdditionalParts(programData: Map[String, Any]): List[AdditionalPart] = {
        var additionalParts = List[AdditionalPart]()

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

            val additionalPart = new AdditionalPart(
                name, estSize, actSize, relativeSize,
                estNumOfMethod, estIsNewReused,
                actNumOfMethod, actIsNewReused,
                methodType)

            // 名前が空の場合は追加しない
            if (name != "") {
                additionalParts = additionalParts :+ additionalPart
            }
        })

        additionalParts
    }

    private def loadReusedParts(programData: Map[String, Any]): List[ReusedPart] = {
        var reusedParts = List[ReusedPart]()

        programData.get("Reused_Objects_List")
                   .getOrElse(List.empty[String])
                   .asInstanceOf[List[String]]
                   .foreach((programNum: String) => {
            val name = getStringValue(programData, s"Reused Objects/$programNum/Description")
            val estSize = getDoubleValue(programData, s"Reused Objects/$programNum/LOC")
            val actSize = getDoubleValue(programData, s"Reused Objects/$programNum/Actual LOC")

            val reusedPart = new ReusedPart(name, estSize, actSize)

            // 名前が空の場合は追加しない
            if (name != "") {
                reusedParts = reusedParts :+ reusedPart
            }
        })

        reusedParts
    }

    private def loadSizeEstimateData(programData: Map[String, Any], key: String): EstimateData = {
        new EstimateData(
            getDoubleValue(programData, s"$key"),
            getStringValue(programData, s"$key/Probe Method"),
            getDoubleValue(programData, s"$key/Beta0"),
            getDoubleValue(programData, s"$key/Beta1"),
            getDoubleValue(programData, s"$key/Interval Percent"),
            getDoubleValue(programData, s"$key/R Squared"),
            getDoubleValue(programData, s"$key/Range"),
            getDoubleValue(programData, s"$key/LPI"),
            getDoubleValue(programData, s"$key/UPI")
        )
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

    private def getDateValue(programData: Map[String, Any], key: String): Date = {
        programData.get(key).getOrElse(new Date(0)).asInstanceOf[Date]
    }
}
