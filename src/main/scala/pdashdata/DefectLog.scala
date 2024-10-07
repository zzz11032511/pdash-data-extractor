package pdashdata

import java.util.Date

class DefectLog(
    val id: Int,
    val defectType: String,
    val injected: String,
    val removed: String,
    val fixTime: Double,
    val fixDefectID: Int,
    val description: String,
    val injectedDate: Date,
) {
    def getID(): Int = id
    def getDefectType(): String = defectType
    def getInjected(): String = injected
    def getRemoved(): String = removed
    def getFixTime(): Double = fixTime
    def getFixDefectID(): Int = fixDefectID
    def getDescription(): String = description
    def getInjectedDate(): Date = injectedDate

    override def toString(): String = {
        s"DefectLog(ID=$id, type=$defectType, injected=$injected, removed=$removed, fixTime=$fixTime, fixDefectID=$fixDefectID, description=$description, injectedDate=$injectedDate)"
    }
}
