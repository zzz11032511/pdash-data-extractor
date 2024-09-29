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
