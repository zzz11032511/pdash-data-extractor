package pdashdata

import java.util.Date

class PhaseData(
    val started : Date,
    val completed : Date,
    val time : Double,
    val estTime : Double,
    val injectDefects : Double,
    val estInjectDefects : Double,
    val removeDefects : Double,
    val estRemoveDefects : Double,
) {
    def getStarted(): Date = started
    def getCompleted(): Date = completed
    def getTime(): Double = time
    def getEstTime(): Double = estTime
    def getInjectDefects(): Double = injectDefects
    def getEstInjectDefects(): Double = estInjectDefects
    def getRemoveDefects(): Double = removeDefects
    def getEstRemoveDefects(): Double = estRemoveDefects
    
    override def toString(): String = {
        s"PhaseData(started=$started, completed=$completed, time=$time, estTime=$estTime, injected=$injectDefects, estInjected=$estInjectDefects, removed=$removeDefects, estRemoved=$estRemoveDefects)"
    }
}
