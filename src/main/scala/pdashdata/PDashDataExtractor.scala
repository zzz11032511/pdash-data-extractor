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
                        if (zipEntry.getSize != 0)
                            timeLogs = LogFileParser.parseTimeLog(input)
                        else
                            timeLogs = List.empty[TimeLog]
                    case "1.dat" =>
                        dataFileMap = dataFileMap ++ Map(1 -> DataFileParser.parseDataFile(input))
                    case "2.dat" =>
                        dataFileMap = dataFileMap ++ Map(2 -> DataFileParser.parseDataFile(input))
                    case "3.dat" =>
                        dataFileMap = dataFileMap ++ Map(3 -> DataFileParser.parseDataFile(input))
                    case "4.dat" =>
                        dataFileMap = dataFileMap ++ Map(4 -> DataFileParser.parseDataFile(input))
                    case "6.dat" =>
                        dataFileMap = dataFileMap ++ Map(5 -> DataFileParser.parseDataFile(input))
                    case "7.dat" =>
                        dataFileMap = dataFileMap ++ Map(6 -> DataFileParser.parseDataFile(input))
                    case "8.dat" =>
                        dataFileMap = dataFileMap ++ Map(7 -> DataFileParser.parseDataFile(input))
                    case "9.dat" =>
                        dataFileMap = dataFileMap ++ Map(8 -> DataFileParser.parseDataFile(input))
                    case "0.def" =>
                        if (zipEntry.getSize != 0)
                            defectLogMap = defectLogMap ++ Map(1 -> LogFileParser.parseDefectLog(input))
                        else
                            defectLogMap = defectLogMap ++ Map(1 -> List.empty[DefectLog])
                    case "1.def" =>
                        if (zipEntry.getSize != 0)
                            defectLogMap = defectLogMap ++ Map(2 -> LogFileParser.parseDefectLog(input))
                        else
                            defectLogMap = defectLogMap ++ Map(2 -> List.empty[DefectLog])
                    case "2.def" =>
                        if (zipEntry.getSize != 0)
                            defectLogMap = defectLogMap ++ Map(3 -> LogFileParser.parseDefectLog(input))
                        else
                            defectLogMap = defectLogMap ++ Map(3 -> List.empty[DefectLog])
                    case "3.def" =>
                        if (zipEntry.getSize != 0)
                            defectLogMap = defectLogMap ++ Map(4 -> LogFileParser.parseDefectLog(input))
                        else
                            defectLogMap = defectLogMap ++ Map(4 -> List.empty[DefectLog])
                    case "5.def" =>
                        if (zipEntry.getSize != 0)
                            defectLogMap = defectLogMap ++ Map(5 -> LogFileParser.parseDefectLog(input))
                        else
                            defectLogMap = defectLogMap ++ Map(5 -> List.empty[DefectLog])
                    case "6.def" =>
                        if (zipEntry.getSize != 0)
                            defectLogMap = defectLogMap ++ Map(6 -> LogFileParser.parseDefectLog(input))
                        else
                            defectLogMap = defectLogMap ++ Map(6 -> List.empty[DefectLog])
                    case "7.def" =>
                        if (zipEntry.getSize != 0)
                            defectLogMap = defectLogMap ++ Map(7 -> LogFileParser.parseDefectLog(input))
                        else
                            defectLogMap = defectLogMap ++ Map(7 -> List.empty[DefectLog])
                    case "8.def" =>
                        if (zipEntry.getSize != 0)
                            defectLogMap = defectLogMap ++ Map(8 -> LogFileParser.parseDefectLog(input))
                        else
                            defectLogMap = defectLogMap ++ Map(8 -> List.empty[DefectLog])
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
        defectLogMap.getOrElse(1, List.empty[DefectLog]) ++
        defectLogMap.getOrElse(2, List.empty[DefectLog]) ++
        defectLogMap.getOrElse(3, List.empty[DefectLog]) ++
        defectLogMap.getOrElse(4, List.empty[DefectLog]) ++
        defectLogMap.getOrElse(5, List.empty[DefectLog]) ++
        defectLogMap.getOrElse(6, List.empty[DefectLog]) ++
        defectLogMap.getOrElse(7, List.empty[DefectLog]) ++
        defectLogMap.getOrElse(8, List.empty[DefectLog])
    }

    private def loadProgramDatas(timeLogs: List[TimeLog], defectLogMap: Map[Int, List[DefectLog]], dataFileMaps: Map[Int, Map[String, Any]]): Map[Int, ProgramData] = {
        var programDatas = Map[Int, ProgramData]()

        dataFileMaps.foreach((num, dataFileMap) => {
            val basePart = loadBaseParts(dataFileMap)
            val additionalPart = loadAdditionalParts(dataFileMap)
            val reusedPart = loadReusedParts(dataFileMap)
            val sizeEstimateData = loadSizeEstimateData(dataFileMap, "Estimated New & Changed LOC")
            val timeEstimateData = loadSizeEstimateData(dataFileMap, "Estimated Time")
            val probeList = dataFileMap.getOrElse("PROBE_LIST", List.empty[String]).asInstanceOf[List[String]]
            val totalSize = getDoubleValue(dataFileMap, "Total LOC")

            val process = num match {
                case 1 => "PSP0"
                case 2 => "PSP0.1"
                case 3 => "PSP1.0"
                case 4 => "PSP1.1"
                case 5 => "PSP2.0"
                case _ => "PSP2.1"
            }

            val programTimeLogs = timeLogs.filter(_.program == s"Program $num")
            val totalTime = programTimeLogs.map(_.delta).sum
            val programDefectLogs = defectLogMap.getOrElse(num, List.empty[DefectLog])
            val phaseDatas = loadPhaseDatas(num, timeLogs, defectLogMap, dataFileMaps)
            val totalDefects = programDefectLogs.map(_.count).sum

            val programData = new ProgramData(
                num, process, programTimeLogs, programDefectLogs, phaseDatas,
                basePart, additionalPart, reusedPart,
                sizeEstimateData, timeEstimateData, probeList, totalSize, totalTime, totalDefects
            )

            programDatas = programDatas + (num -> programData)
        })

        programDatas
    }

    private def loadPhaseDatas(num: Int, timeLogs: List[TimeLog], defectLogMap: Map[Int, List[DefectLog]], dataFileMap: Map[Int, Map[String, Any]]): Map[String, PhaseData] = {
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

    private def loadPhaseData(num: Int, phase: String, timeLogs: List[TimeLog], defectLogMap: Map[Int, List[DefectLog]], dataFileMap: Map[Int, Map[String, Any]]): PhaseData = {
        val started = getDateValue(dataFileMap(num), s"$phase/Started")
        val completed = getDateValue(dataFileMap(num), s"$phase/Completed")
        val time = getDoubleValue(dataFileMap(num), s"$phase/Time")
        val injectedDefects = getDoubleValue(dataFileMap(num), s"$phase/Defects Injected")
        val removedDefects = getDoubleValue(dataFileMap(num), s"$phase/Defects Removed")

        var estTime = getDoubleValue(dataFileMap(num), s"$phase/Estimated Time")
        if (estTime == 0.0) {
            if (1 < num && num <= 4) {
                val phaseCumTime = (1 until num).map(i =>
                    timeLogs.filter(_.program == s"Program $i")
                      .filter(_.phase == phase)
                      .map(_.delta)
                      .sum
                ).sum
                val cumTime = (1 until num).map(i =>
                    timeLogs.filter(_.program == s"Program $i")
                      .map(_.delta)
                      .sum
                ).sum
                val estTotalTime = getDoubleValue(dataFileMap(num), "Estimated Time")

                estTime = (phaseCumTime / cumTime) * estTotalTime
            } else if (5 <= num && num <= 8) {
                val phaseCumTime = (5 until num).map(i =>
                    timeLogs.filter(_.program == s"Program $i")
                      .filter(_.phase == phase)
                      .map(_.delta)
                      .sum
                ).sum
                val cumTime = (5 until num).map(i =>
                    timeLogs.filter(_.program == s"Program $i")
                      .map(_.delta)
                      .sum
                ).sum
                val estTotalTime = getDoubleValue(dataFileMap(num), "Estimated Time")

                estTime = (phaseCumTime / cumTime) * estTotalTime
            }
        }

        var estInjectedDefects = getDoubleValue(dataFileMap(num), s"$phase/Estimated Defects Injected")
        var estRemovedDefects = getDoubleValue(dataFileMap(num), s"$phase/Estimated Defects Removed")
        if (estInjectedDefects == 0.0 && num >= 5) {
            val phaseCumInjDefects = if (num == 5) {
                (1 until num).map(i =>
                    defectLogMap(i).filter(_.injected == phase)
                      .map(_.count)
                      .sum
                ).sum
            } else {
                (5 until num).map(i =>
                    defectLogMap(i).filter(_.injected == phase)
                      .map(_.count)
                      .sum
                ).sum
            }

            val phaseCumRemDefects = if (num == 5) {
                (1 until num).map(i =>
                    defectLogMap(i).filter(_.removed == phase)
                      .map(_.count)
                      .sum
                ).sum
            } else {
                (5 until num).map(i =>
                    defectLogMap(i).filter(_.removed == phase)
                      .map(_.count)
                      .sum
                ).sum
            }

            val cumDefects = if (num == 5) {
                (1 until num).map(i =>
                    defectLogMap(i).map(_.count).sum
                ).sum
            } else {
                (5 until num).map(i =>
                    defectLogMap(i).map(_.count).sum
                ).sum
            }

            val cumTotalSize = if (num == 5) {
                (3 until num).map(i => {
                    val cumAdded = loadBaseParts(dataFileMap(i)).map(_.actAdded).sum
                    val cumModified = loadBaseParts(dataFileMap(i)).map(_.actModified).sum
                    val cumPartsAdded = loadAdditionalParts(dataFileMap(i)).map(_.actSize).sum
                    cumAdded + cumModified + cumPartsAdded
                }).sum + getDoubleValue(dataFileMap(1), "Total LOC") + getDoubleValue(dataFileMap(2), "Total LOC")
            } else {
                (5 until num).map(i => {
                    val cumAdded = loadBaseParts(dataFileMap(i)).map(_.actAdded).sum
                    val cumModified = loadBaseParts(dataFileMap(i)).map(_.actModified).sum
                    val cumPartsAdded = loadAdditionalParts(dataFileMap(i)).map(_.actSize).sum
                    cumAdded + cumModified + cumPartsAdded
                }).sum
            }
            val estTotalSize = getDoubleValue(dataFileMap(num), "Estimated New & Changed LOC")

            val estTotalInjectedDefects = (cumDefects / cumTotalSize) * estTotalSize
            estInjectedDefects = (phaseCumInjDefects / cumDefects) * estTotalInjectedDefects
            if (estRemovedDefects == 0.0) {
                estRemovedDefects = (phaseCumRemDefects / cumDefects) * estTotalInjectedDefects
            }
        }

        new PhaseData(
            started, completed, time, estTime, 
            injectedDefects, estInjectedDefects, removedDefects, estRemovedDefects
        )
    }

    private def loadBaseParts(programData: Map[String, Any]): List[BasePart] = {
        var baseParts = List[BasePart]()

        programData.getOrElse("Base_Parts_List", List.empty[String])
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

        programData.getOrElse("New_Objects_List", List.empty[String])
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

        programData.getOrElse("Reused_Objects_List", List.empty[String])
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
        programData.getOrElse(key, "").asInstanceOf[String]
    }

    private def getDoubleValue(programData: Map[String, Any], key: String): Double = {
        programData.getOrElse(key, 0.0).asInstanceOf[Double]
    }

    private def getIntValue(programData: Map[String, Any], key: String): Int = {
        getDoubleValue(programData, key).toInt
    }

    private def getDateValue(programData: Map[String, Any], key: String): Date = {
        programData.getOrElse(key, new Date(0)).asInstanceOf[Date]
    }
}
