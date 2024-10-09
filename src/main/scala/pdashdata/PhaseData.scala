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
        s"PhaseData(started=$started, completed=$completed, time=$time, estTime=$estTime," + 
        s"injected=$injectedDefects, estInjected=$estInjectedDefects, removed=$removedDefects, estRemoved=$estRemovedDefects)"
    }
}
