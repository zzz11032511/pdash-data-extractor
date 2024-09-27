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
    override def toString(): String = {
        s"AdditionalParts(name=$name, estSize=$estSize, actSize=$actSize, relativeSize=$relativeSize, estNumOfMethod=$estNumOfMethod, estIsNewReused=$estIsNewReused, actNumOfMethod=$actNumOfMethod, actIsNewReused=$actIsNewReused, methodType=$methodType)"
    }
}
