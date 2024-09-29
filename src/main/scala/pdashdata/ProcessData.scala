package pdashdata

import scala.jdk.CollectionConverters._

class ProcessData(
    val timeLogs: List[TimeLog], 
    val defectLogs: List[DefectLog], 
    val programDatas: Map[Int, ProgramData]
) {
    def getTimeLogs(): List[TimeLog] = timeLogs
    def getTimeLogsAsJava(): java.util.List[TimeLog] = timeLogs.asJava

    def getDefectLogs(): List[DefectLog] = defectLogs
    def getDefectLogsAsJava(): java.util.List[DefectLog] = defectLogs.asJava

    def getProgramDatas(): Map[Int, ProgramData] = programDatas
}
