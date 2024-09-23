package pdashdata

import java.util.Map
import java.util.HashMap
import java.util.List

class ProcessData(var timeLogs: List[TimeLog], var defectLogs: Map[Int, List[DefectLog]], var programDatas: Map[Int, Map[String, Any]]) {
    def getTimeLogs(): List[TimeLog] = timeLogs
    def getDefectLogs(): Map[Int, List[DefectLog]] = defectLogs
    def getProgramDatas(): Map[Int, Map[String, Any]] = programDatas
    
    def getTotalLOCs(): Map[Int, Int] = {
        var totalLOCs = new HashMap[Int, Int]()
        for (i <- 1 to 8) {
            totalLOCs.put(i, programDatas.get(i).get("Total LOC").asInstanceOf[Double].toInt)
        }
        totalLOCs
    }
}
