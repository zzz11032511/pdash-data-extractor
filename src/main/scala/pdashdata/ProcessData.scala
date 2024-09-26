package pdashdata

import scala.jdk.CollectionConverters._

class ProcessData(
    val timeLogs: List[TimeLog], 
    val defectLogs: Map[Int, List[DefectLog]], 
    val programDatas: Map[Int, Map[String, Any]]
) {
    def getTimeLogs(): List[TimeLog] = timeLogs
    def getTimeLogsAsJava(): java.util.List[TimeLog] = timeLogs.asJava

    def getDefectLogs(): Map[Int, List[DefectLog]] = defectLogs
    def getDefectLogsAsJava(): java.util.Map[Int, java.util.List[DefectLog]] = {
        var defectLogsJava = Map[Int, java.util.List[DefectLog]]()
        defectLogs.foreach((k, v) => {
            defectLogsJava += (k -> v.asJava)
        })
        defectLogsJava.asJava
    }
    
    def getTotalLOCs(): Map[Int, Int] = {
        var totalLOCs = Map[Int, Int]()
        programDatas.foreach((k, v) => {
            val totalLOC = v.get("Total LOC").getOrElse(0.0).asInstanceOf[Double].toInt
            totalLOCs += (k -> totalLOC)
        })
        totalLOCs
    }
    def getTotalLOCsAsJava(): java.util.Map[Int, Int] = getTotalLOCs().asJava
}
