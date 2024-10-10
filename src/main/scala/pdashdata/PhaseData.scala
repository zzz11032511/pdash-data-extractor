package pdashdata

import java.util.Date

class PhaseData(
    val started : Date,
    val completed : Date,
    val time : Double,
    val estTime : Double,
    val injectedDefects : Double,
    val estInjectedDefects : Double,
    val removedDefects : Double,
    val estRemovedDefects : Double,
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
        f"PhaseData(started=$started, completed=$completed, time=$time%3.0f, estTime=$estTime%3.0f, " +
        f"injected=$injectedDefects%2.2f, estInjected=$estInjectedDefects%2.2f, " +
        f"removed=$removedDefects%2.2f, estRemoved=$estRemovedDefects%2.2f)"
    }
}
