package pdashdata

import java.util.Date

class PhaseData(
    val started : Date,   // 開始日時
    val completed : Date, // 終了日時
    val time : Double,    // 実績時間
    val estTime : Double, // 計画時間
    val injectedDefects : Double,    // 埋め込んだ欠陥数
    val estInjectedDefects : Double, // 計画時点での埋め込み欠陥数
    val removedDefects : Double,     // 除去した欠陥数
    val estRemovedDefects : Double,  // 計画時点での除去欠陥数
) {
    def getStarted(): Date = started
    def getCompleted(): Date = completed
    def getTime(): Double = time
    def getEstTime(): Double = estTime
    def getInjectDefects(): Double = injectedDefects
    def getEstInjectDefects(): Double = estInjectedDefects
    def getRemoveDefects(): Double = removedDefects
    def getEstRemoveDefects(): Double = estRemovedDefects
    
    override def toString(): String = {
        f"PhaseData: time=$time%3.0f, estTime=$estTime%3.0f, " +
        f"injected=$injectedDefects%2.2f, estInjected=$estInjectedDefects%2.2f, " +
        f"removed=$removedDefects%2.2f, estRemoved=$estRemovedDefects%2.2f)"
    }
}
