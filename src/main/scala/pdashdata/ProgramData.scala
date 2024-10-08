package pdashdata

import scala.jdk.CollectionConverters._

class ProgramData(
    val num: Int,
    val process: String,
    val timeLogs: List[TimeLog],
    val defectLogs: List[DefectLog],
    val phaseDatas: Map[String, PhaseData],
    val baseParts: List[BasePart],
    val additionalParts: List[AdditionalPart],
    val reusedParts: List[ReusedPart],
    val sizeEstimateData: EstimateData,
    val timeEstimateData: EstimateData,
    val probeList: List[String],
    val totalSize: Double,
    val totalTime: Double
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
}
