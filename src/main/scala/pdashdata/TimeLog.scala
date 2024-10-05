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
    override def toString(): String = {
        s"TimeLog(ID=$id, $program, $phase, $startTime, delta=$delta, int=$interrupt, $comment)"
    }
}
