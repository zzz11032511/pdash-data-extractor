package pdashdata

import java.util.Date

class DefectLog(
    val id: Int, // ログのID
    val programNumber: Int,  // 欠陥が発生したプログラム番号
    val count: Double,       // 欠陥の数
    val defectType: String,  // 欠陥型
    val injected: String,    // 欠陥の埋め込みフェーズ
    val removed: String,     // 欠陥の除去フェーズ
    val fixTime: Double,     // 欠陥の修正時間
    val fixDefectID: Int,    // 修正欠陥のID
    val description: String, // 欠陥の説明
    val injectedDate: Date,  // 欠陥発生日
) {
    def getID(): Int = id
    def getProgramNumber(): Int = programNumber
    def getCount(): Double = count
    def getDefectType(): String = defectType
    def getInjected(): String = injected
    def getRemoved(): String = removed
    def getFixTime(): Double = fixTime
    def getFixDefectID(): Int = fixDefectID
    def getDescription(): String = description
    def getInjectedDate(): Date = injectedDate

    override def toString(): String = {
        s"DefectLog: ID=$id, Program $programNumber, count=$count, type=$defectType, injected=$injected, removed=$removed, " +
        s"fixTime=$fixTime, fixDefectID=$fixDefectID, desc=$description, date=$injectedDate)"
    }
}
