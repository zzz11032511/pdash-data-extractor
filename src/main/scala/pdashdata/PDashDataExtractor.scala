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
        val (timeLogs, defectLogs, dataFileMap) = loadProcessDataZip(path)
        val programDatas = loadProgramDatas(timeLogs, defectLogs, dataFileMap)

        new ProcessData(timeLogs, defectLogs, programDatas)
    }

    /**
     * プロセスデータのZIPファイルを読み込み
     * 時間ログ、欠陥ログ、課題ごとのデータファイルをまとめたタプルとして返す
     */
    private def loadProcessDataZip(path: String): (List[TimeLog], List[DefectLog], Map[Int, Map[String, Any]]) = {
        var timeLogs: List[TimeLog] = null
        var defectLogMap: Map[Int, List[DefectLog]] = Map[Int, List[DefectLog]]()
        var dataFileMap: Map[Int, Map[String, Any]] = Map[Int, Map[String, Any]]()

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
                    // zipEntry.getSizeが0の場合は、欠陥ログが無い(欠陥が発生していない)ので空のリストを追加する
                    if (zipEntry.getSize != 0)
                        defectLogMap = defectLogMap ++ Map(1 -> LogFileParser.parseDefectLog(input, 1))
                    else
                        defectLogMap = defectLogMap ++ Map(1 -> List.empty[DefectLog])
                case "1.def" =>
                    if (zipEntry.getSize != 0)
                        defectLogMap = defectLogMap ++ Map(2 -> LogFileParser.parseDefectLog(input, 2))
                    else
                        defectLogMap = defectLogMap ++ Map(2 -> List.empty[DefectLog])
                case "2.def" =>
                    if (zipEntry.getSize != 0)
                        defectLogMap = defectLogMap ++ Map(3 -> LogFileParser.parseDefectLog(input, 3))
                    else
                        defectLogMap = defectLogMap ++ Map(3 -> List.empty[DefectLog])
                case "3.def" =>
                    if (zipEntry.getSize != 0)
                        defectLogMap = defectLogMap ++ Map(4 -> LogFileParser.parseDefectLog(input, 4))
                    else
                        defectLogMap = defectLogMap ++ Map(4 -> List.empty[DefectLog])
                case "5.def" =>
                    if (zipEntry.getSize != 0)
                        defectLogMap = defectLogMap ++ Map(5 -> LogFileParser.parseDefectLog(input, 5))
                    else
                        defectLogMap = defectLogMap ++ Map(5 -> List.empty[DefectLog])
                case "6.def" =>
                    if (zipEntry.getSize != 0)
                        defectLogMap = defectLogMap ++ Map(6 -> LogFileParser.parseDefectLog(input, 6))
                    else
                        defectLogMap = defectLogMap ++ Map(6 -> List.empty[DefectLog])
                case "7.def" =>
                    if (zipEntry.getSize != 0)
                        defectLogMap = defectLogMap ++ Map(7 -> LogFileParser.parseDefectLog(input, 7))
                    else
                        defectLogMap = defectLogMap ++ Map(7 -> List.empty[DefectLog])
                case "8.def" =>
                    if (zipEntry.getSize != 0)
                        defectLogMap = defectLogMap ++ Map(8 -> LogFileParser.parseDefectLog(input, 8))
                    else
                        defectLogMap = defectLogMap ++ Map(8 -> List.empty[DefectLog])
                case _ => null
            }
            zipEntry = zipInputStream.getNextEntry()
        }

        // 欠陥ログを1つのリストにまとめてからデータを返す
        (timeLogs, flatDefectLogMap(defectLogMap), dataFileMap)
    }

    /**
     * 欠陥ログのMapを1つのリストにまとめる
     */
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

    /**
     * プログラムデータを読み込む
     */
    private def loadProgramDatas(timeLogs: List[TimeLog], defectLogs: List[DefectLog], dataFileMaps: Map[Int, Map[String, Any]]): Map[Int, ProgramData] = {
        var programDatas = Map[Int, ProgramData]()

        dataFileMaps.foreach((num, dataFile) => {
            val basePart = loadBaseParts(dataFile)
            val additionalPart = loadAdditionalParts(dataFile)
            val reusedPart = loadReusedParts(dataFile)
            val sizeEstimateData = loadSizeEstimateData(dataFile, "Estimated New & Changed LOC")
            val timeEstimateData = loadSizeEstimateData(dataFile, "Estimated Time")
            val probeList = dataFile.getOrElse("PROBE_LIST", List.empty[String]).asInstanceOf[List[String]]
            val totalSize = getDoubleValue(dataFile, "Total LOC")

            val process = num match {
                case 1 => "PSP0"
                case 2 => "PSP0.1"
                case 3 => "PSP1.0"
                case 4 => "PSP1.1"
                case 5 => "PSP2.0"
                case _ => "PSP2.1"
            }

            val programTimeLogs = timeLogs.filter(_.programNumber == num)
            val totalTime = programTimeLogs.map(_.delta).sum
            val programDefectLogs = defectLogs.filter(_.programNumber == num)
            val totalDefects = programDefectLogs.map(_.count).sum

            val phaseDatas = loadPhaseDatas(num, timeLogs, defectLogs, dataFileMaps)

            val programData = new ProgramData(
                num, process, programTimeLogs, programDefectLogs, phaseDatas,
                basePart, additionalPart, reusedPart,
                sizeEstimateData, timeEstimateData, probeList, totalSize, totalTime, totalDefects
            )

            programDatas = programDatas + (num -> programData)
        })

        programDatas
    }

    /**
     * 指定したプログラム番号における全フェーズのデータのMapを読み込む
     */
    private def loadPhaseDatas(num: Int, timeLogs: List[TimeLog], defectLogs: List[DefectLog], dataFileMap: Map[Int, Map[String, Any]]): Map[String, PhaseData] = {
        var phaseDatas = Map[String, PhaseData]()

        phaseDatas = phaseDatas
            + ("Planning" -> loadPhaseData(num, "Planning", timeLogs, defectLogs, dataFileMap))
        phaseDatas = phaseDatas
            + ("Design" -> loadPhaseData(num, "Design", timeLogs, defectLogs, dataFileMap))
        phaseDatas = phaseDatas
            + ("Design Review" -> loadPhaseData(num, "Design Review", timeLogs, defectLogs, dataFileMap))
        phaseDatas = phaseDatas
            + ("Code" -> loadPhaseData(num, "Code", timeLogs, defectLogs, dataFileMap))
        phaseDatas = phaseDatas
            + ("Code Review" -> loadPhaseData(num, "Code Review", timeLogs, defectLogs, dataFileMap))
        phaseDatas = phaseDatas
            + ("Compile" -> loadPhaseData(num, "Compile", timeLogs, defectLogs, dataFileMap))
        phaseDatas = phaseDatas
            + ("Test" -> loadPhaseData(num, "Test", timeLogs, defectLogs, dataFileMap))
        phaseDatas = phaseDatas
            + ("Postmortem" -> loadPhaseData(num, "Postmortem", timeLogs, defectLogs, dataFileMap))
        phaseDatas = phaseDatas
            + ("After Development" -> loadPhaseData(num, "After Development", timeLogs, defectLogs, dataFileMap))

        phaseDatas
    }

    /**
     * フェーズごとのデータを読み込む
     */
    private def loadPhaseData(num: Int, phase: String, timeLogs: List[TimeLog], defectLogs: List[DefectLog], dataFileMap: Map[Int, Map[String, Any]]): PhaseData = {
        val started = getDateValue(dataFileMap(num), s"$phase/Started")
        val completed = getDateValue(dataFileMap(num), s"$phase/Completed")
        val time = getDoubleValue(dataFileMap(num), s"$phase/Time")
        val injectedDefects = getDoubleValue(dataFileMap(num), s"$phase/Defects Injected")
        val removedDefects = getDoubleValue(dataFileMap(num), s"$phase/Defects Removed")

        var estTime = getDoubleValue(dataFileMap(num), s"$phase/Estimated Time")
        if (estTime == 0.0) {
            if (2 <= num && num <= 4) {
                val phaseCumTime = cumulativeTimeBetween(1, num - 1, phase, timeLogs)
                val cumTime = cumulativeTotalTimeBetween(1, num - 1, timeLogs)
                val estTotalTime = getDoubleValue(dataFileMap(num), "Estimated Time")

                estTime = (phaseCumTime / cumTime) * estTotalTime
            } else if (num >= 5) {
                // PSP2では課題5以降のデータを利用する
                // 課題5では過去の実績が存在しないため手動で入力したデータが保存されているはず
                val phaseCumTime = cumulativeTimeBetween(5, num - 1, phase, timeLogs)
                val cumTime = cumulativeTotalTimeBetween(5, num - 1, timeLogs)
                val estTotalTime = getDoubleValue(dataFileMap(num), "Estimated Time")

                estTime = (phaseCumTime / cumTime) * estTotalTime
            }
        }

        var estInjectedDefects = getDoubleValue(dataFileMap(num), s"$phase/Estimated Defects Injected")
        var estRemovedDefects = getDoubleValue(dataFileMap(num), s"$phase/Estimated Defects Removed")
        if (num == 5) {
            // 課題5ではPSP2のデータがないため、課題1から4のデータを利用する
            val phaseCumInjDefects = cumulativeInjectedDefectsBetween(1, 4, phase, defectLogs)
            val phaseCumRemDefects = cumulativeRemovedDefectsBetween(1, 4, phase, defectLogs)
            val cumDefects = cumlativeTotalDefectsBetween(1, 4, defectLogs)

            val cumTotalNandCSize = cumlativeAddedAndModifiedSizeBetween(1, 4, dataFileMap)
            val estTotalNandCSize = getDoubleValue(dataFileMap(5), "Estimated New & Changed LOC")

            val estTotalInjectedDefects = (cumDefects / cumTotalNandCSize) * estTotalNandCSize
            // println(s"Program $num, $phase, $estTotalInjectedDefects, $cumDefects, $cumTotalNandCSize, $estTotalNandCSize")

            if (estInjectedDefects == 0.0 && phaseCumInjDefects != 0.0) {
                // もともと入っていた値が0.0かつ、実際の計算結果が0.0でない場合は
                // Process Dashboardがデータとして保存せずに毎回計算しているので、上記の計算結果を利用する
                // 基本的にはこのケースだが、課題5のように手動で計算する場合はデータとして保存されているので
                // 計算結果は使用せずに保存されていた値を利用する
                estInjectedDefects = (phaseCumInjDefects / cumDefects) * estTotalInjectedDefects
            }
            if (estRemovedDefects == 0.0 && phaseCumRemDefects != 0.0) {
                estRemovedDefects = (phaseCumRemDefects / cumDefects) * estTotalInjectedDefects
            }
        } else if (num >= 6) {
            // 課題6以降ではPSP2のデータを利用する
            val phaseCumInjDefects = cumulativeInjectedDefectsBetween(5, num - 1, phase, defectLogs)
            val phaseCumRemDefects = cumulativeRemovedDefectsBetween(5, num - 1, phase, defectLogs)
            val cumDefects = cumlativeTotalDefectsBetween(5, num - 1, defectLogs)

            val cumTotalNandCSize = cumlativeAddedAndModifiedSizeBetween(5, num - 1, dataFileMap)
            val estTotalNandCSize = getDoubleValue(dataFileMap(num), "Estimated New & Changed LOC")

            val estTotalInjectedDefects = (cumDefects / cumTotalNandCSize) * estTotalNandCSize
            // println(s"Program $num, $phase, $estTotalInjectedDefects, $cumDefects, $cumTotalNandCSize, $estTotalNandCSize")

            if (estInjectedDefects == 0.0 && phaseCumInjDefects != 0.0) {
                estInjectedDefects = (phaseCumInjDefects / cumDefects) * estTotalInjectedDefects
            }
            if (estRemovedDefects == 0.0 && phaseCumRemDefects != 0.0) {
                estRemovedDefects = (phaseCumRemDefects / cumDefects) * estTotalInjectedDefects
            }
        }

        new PhaseData(
            started, completed, time, estTime, 
            injectedDefects, estInjectedDefects, removedDefects, estRemovedDefects
        )
    }

    /**
     * 指定した課題までのフェーズごとの累積時間を返す
     */
    private def cumulativeTimeBetween(from: Int, to: Int, phase: String, timeLogs: List[TimeLog]): Double = {
        (from to to).map(i => timeLogs.filter(_.programNumber == i).filter(_.phase == phase).map(_.delta).sum).sum
    }

    /**
     * 指定した課題までの累積時間を返す
     */
    private def cumulativeTotalTimeBetween(from: Int, to: Int, timeLogs: List[TimeLog]): Double = {
        (from to to).map(i => timeLogs.filter(_.programNumber == i).map(_.delta).sum).sum
    }

    /**
     * 指定した課題範囲におけるフェーズごとの埋め込まれた欠陥の累積値を返す
     */
    private def cumulativeInjectedDefectsBetween(from: Int, to: Int, phase: String, defectLogs: List[DefectLog]): Double = {
        (from to to).map(i => defectLogs.filter(_.programNumber == i).filter(_.injected == phase).map(_.count).sum).sum
    }

    /**
     * 指定した課題範囲におけるフェーズごとの除去した欠陥の累積値を返す
     */
    private def cumulativeRemovedDefectsBetween(from: Int, to: Int, phase: String, defectLogs: List[DefectLog]): Double = {
        (from to to).map(i => defectLogs.filter(_.programNumber == i).filter(_.removed == phase).map(_.count).sum).sum
    }

    /**
     * 指定した課題範囲で発生した欠陥の累積値を返す
     */
    private def cumlativeTotalDefectsBetween(from: Int, to: Int, defectLogs: List[DefectLog]): Double = {
        (from to to).map(i => defectLogs.filter(_.programNumber == i).map(_.count).sum).sum
    }

    /**
     * 指定した課題範囲でAdd And Modified規模の累積値を返す
     */
    private def cumlativeAddedAndModifiedSizeBetween(from: Int, to: Int, dataFileMap: Map[Int, Map[String, Any]]): Double = {
        (from to to).map(i => {
            if (i == 1 || i == 2) {
                // 課題1と2では簡単な規模見積りしかせず、再利用部品などを考慮する必要がないため
                // Total LOCに保存されている値をそのまま利用する

                // println(s"Program $i ${getDoubleValue(dataFileMap(i), "Total LOC")}")
                getDoubleValue(dataFileMap(i), "Total LOC")
            } else {
                val cumAdded = loadBaseParts(dataFileMap(i)).map(_.actAdded).sum
                val cumModified = loadBaseParts(dataFileMap(i)).map(_.actModified).sum
                val cumPartsAdded = loadAdditionalParts(dataFileMap(i)).map(_.actSize).sum
                // println(s"Program $i ${cumAdded + cumModified + cumPartsAdded}")
                cumAdded + cumModified + cumPartsAdded            
            }
        }).sum
    }

    /**
     * ベース部品のデータを読み込む
     */
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

            // 名前が空の場合は部品として追加しない
            if (name != "") {
                baseParts = baseParts :+ basePart
            }
        })

        baseParts
    }

    /**
     * 追加部品のデータを読み込む
     */
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

    /**
     * 再利用部品のデータを読み込む
     */
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

    /**
     * 規模見積りデータを読み込む
     */
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
