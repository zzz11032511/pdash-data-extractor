package pdashdata

import java.util.Date

class TimeLog(
    val id: Int,  // ログのID
    val programNumber: Int, // プログラム番号
    val phase: String,      // フェーズ名
    val startTime: Date,    // 開始時刻
    val delta: Double,      // 経過時間
    val interrupt: Double,  // 中断時間
    val comment: String,    // コメント
) {
    def getID(): Int = id
    def getProgramNumber(): Int = programNumber
    def getPhase(): String = phase
    def getStartTime(): Date = startTime
    def getDelta(): Double = delta
    def getInterrupt(): Double = interrupt
    def getComment(): String = comment
    
    override def toString(): String = {
        s"TimeLog: ID=$id, Program $programNumber, $phase, $startTime, delta=$delta, int=$interrupt " +
          s"${if comment.isEmpty then "" else s"$comment"})"
    }
}
