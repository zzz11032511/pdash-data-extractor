import java.util.Date

class DefectData(
    val ID: Int,
    val defectType: String,
    val injected: String,
    val removed: String,
    val fixTime: Double,
    val fixDefectID: Int,
    val description: String,
    val injectedDate: Date,
) {

}
