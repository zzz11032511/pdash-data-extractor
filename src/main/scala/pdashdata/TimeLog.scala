import java.util.Date

class TimeLog(
    val ID: Int,
    val program: String,
    val phase: String,
    val startTime: Date,
    val delta: Double,
    val interrupt: Double,
    val comment: String,
) {
    override def toString(): String = {
        s"TimeLog($ID, $program, $phase, $startTime, $delta, $interrupt, $comment)"
    }
}
