package pdashdata

import java.util.Map
import java.util.List

class ProcessData(var timeLogs: List[TimeLog], var defectLogs: Map[Int, List[DefectLog]], var programDatas: Map[Int, Map[String, Any]]) {
    def getTimeLogs(): List[TimeLog] = timeLogs
    def getDefectLogs(): Map[Int, List[DefectLog]] = defectLogs
    def getProgramDatas(): Map[Int, Map[String, Any]] = programDatas
}
