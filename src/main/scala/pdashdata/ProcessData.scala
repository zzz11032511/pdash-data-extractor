package pdashdata

import scala.jdk.CollectionConverters._

class ProcessData(
    val timeLogs: List[TimeLog], 
    val defectLogs: List[DefectLog], 
    val programDatas: Map[Int, ProgramData]
) {
    def getTimeLogs(): java.util.List[TimeLog] = timeLogs.asJava
    def getDefectLogs(): java.util.List[DefectLog] = defectLogs.asJava
    def getProgramDatas(): java.util.Map[Int, ProgramData] = programDatas.asJava
}
