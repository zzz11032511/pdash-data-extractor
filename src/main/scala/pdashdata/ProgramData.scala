package pdashdata

import scala.jdk.CollectionConverters._

class ProgramData(
    val number: Int, // 課題番号
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
    def getNumber(): Int = number
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

    def getAddAndModified(): Double = {
        if (number == 1 || number == 2) {
            // 課題1と2では簡単な規模見積りしかせず、再利用部品などを考慮する必要がないため
            // Total LOCに保存されている値をそのまま利用する
            totalSize
        } else {
            val cumAdded = baseParts.map(_.actAdded).sum
            val cumModified = baseParts.map(_.actModified).sum
            val cumPartsAdded = additionalParts.map(_.actSize).sum
            cumAdded + cumModified + cumPartsAdded            
        }
    }

    def getProductivity(): Double = {
        val addAndModified = getAddAndModified()
        if (addAndModified == 0) {
            0
        } else {
            addAndModified / (totalTime / 60.0)
        }
    }

    def getSizeEstimatingError(): Double = {
        if (number == 1) {
            Double.NaN
        } else {
            (sizeEstimateData.value / getAddAndModified() - 1) * 100
        }
    }

    def getTimeEstimateError(): Double = {
        (timeEstimateData.value / totalTime - 1) * 100
    }
}
