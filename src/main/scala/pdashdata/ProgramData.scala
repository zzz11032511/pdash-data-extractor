package pdashdata

import scala.jdk.CollectionConverters._

class ProgramData(
    val num: Int, // 課題番号
    val process: String, // PSPプロセス
    val timeLogs: List[TimeLog],     // 時間ログ
    val defectLogs: List[DefectLog], // 欠陥ログ
    val phaseDatas: Map[String, PhaseData], // フェーズごとのデータ
    val baseParts: List[BasePart],             // ベース部品
    val additionalParts: List[AdditionalPart], // 追加部品
    val reusedParts: List[ReusedPart],         // 再利用部品
    val sizeEstimateData: EstimateData, // 規模見積データ
    val timeEstimateData: EstimateData, // 時間見積データ
    val probeList: List[String], // PROBE手法で利用した課題のリスト
    val totalSize: Double,    // 合計規模
    val totalTime: Double,    // 合計時間
    val totalDefects: Double, // 合計欠陥数
) {
    def getNum(): Int = num
    def getProcess(): String = process
    def getTimeLogs(): java.util.List[TimeLog] = timeLogs.asJava
    def getDefectLogs(): java.util.List[DefectLog] = defectLogs.asJava
    def getPhaseDatas(): java.util.Map[String, PhaseData] = phaseDatas.asJava
    def getBaseParts(): java.util.List[BasePart] = baseParts.asJava
    def getAdditionalParts(): java.util.List[AdditionalPart] = additionalParts.asJava
    def getReusedParts(): java.util.List[ReusedPart] = reusedParts.asJava
    def getSizeEstimateData(): EstimateData = sizeEstimateData
    def getTimeEstimateData(): EstimateData = timeEstimateData
    def getProbeList(): java.util.List[String] = probeList.asJava
    def getTotalSize(): Double = totalSize
    def getTotalTime(): Double = totalTime
    def getTotalDefects(): Double = totalDefects
}
