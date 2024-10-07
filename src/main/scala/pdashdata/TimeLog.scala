package pdashdata

import java.util.Date

class TimeLog(
    val id: Int,
    val program: String,
    val phase: String,
    val startTime: Date,
    val delta: Double,
    val interrupt: Double,
    val comment: String,
) {
    def getID(): Int = id
    def getProgram(): String = program
    def getPhase(): String = phase
    def getStartTime(): Date = startTime
    def getDelta(): Double = delta
    def getInterrupt(): Double = interrupt
    def getComment(): String = comment
    
    override def toString(): String = {
        s"TimeLog(ID=$id, $program, $phase, $startTime, delta=$delta, int=$interrupt, $comment)"
    }
}
