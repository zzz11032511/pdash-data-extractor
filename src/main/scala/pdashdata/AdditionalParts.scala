package pdashdata

class AdditionalParts(
    val name: String,
    val estSize: Double,
    val actSize: Double,
    val relativeSize: String,
    val estNumOfMethod: Int,
    val estIsNewReused: Boolean,
    val actNumOfMethod: Int,
    val actIsNewReused: Boolean,
    val methodType: String,
) {
}
