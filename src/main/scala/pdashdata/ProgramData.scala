package pdashdata

import scala.jdk.CollectionConverters._

class ProgramData(
    val num: Int,
    val timeLogs: List[TimeLog],
    val defectLogs: List[DefectLog],
    val baseParts: List[BasePart],
    val additionalParts: List[AdditionalPart],
    val reusedParts: List[ReusedPart],
    val sizeEstimateData: EstimateData,
    val timeEstimateData: EstimateData,
    val probeList: List[String],
    val totalSize: Int,
) {
    def getNum(): Int = num
    
    def getTimeLogs(): List[TimeLog] = timeLogs
    def getTimeLogsAsJava(): java.util.List[TimeLog] = timeLogs.asJava

    def getDefectLogs(): List[DefectLog] = defectLogs
    def getDefectLogsAsJava(): java.util.List[DefectLog] = defectLogs.asJava

    def getBaseParts(): List[BasePart] = baseParts
    def getBasePartsAsJava(): java.util.List[BasePart] = baseParts.asJava

    def getAdditionalParts(): List[AdditionalPart] = additionalParts
    def getAdditionalPartsAsJava(): java.util.List[AdditionalPart] = additionalParts.asJava

    def getReusedParts(): List[ReusedPart] = reusedParts
    def getReusedPartsAsJava(): java.util.List[ReusedPart] = reusedParts.asJava

    def getSizeEstimateData(): EstimateData = sizeEstimateData
    def getTimeEstimateData(): EstimateData = timeEstimateData

    def getProbeList(): List[String] = probeList
    def getProbeListAsJava(): java.util.List[String] = probeList.asJava

    def getTotalSize(): Int = totalSize

    override def toString(): String = {
        s"Program $num \n" +
        s"TimeLogs: $timeLogs \n" +
        s"DefectLogs: $defectLogs \n" +
        s"BaseParts: $baseParts \n" +
        s"AdditionalParts: $additionalParts \n" +
        s"ReusedParts: $reusedParts \n" +
        s"SizeEstimateData: $sizeEstimateData \n" +
        s"TimeEstimateData: $timeEstimateData \n" +
        s"ProbeList: $probeList \n" +
        s"TotalSize: $totalSize"
    }
}
