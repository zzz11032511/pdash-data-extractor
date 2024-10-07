package pdashdata

class AdditionalPart(
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
    def getName(): String = name
    def getEstSize(): Double = estSize
    def getActSize(): Double = actSize
    def getRelativeSize(): String = relativeSize
    def getEstNumOfMethod(): Int = estNumOfMethod
    def getEstIsNewReused(): Boolean = estIsNewReused
    def getActNumOfMethod(): Int = actNumOfMethod
    def getActIsNewReused(): Boolean = actIsNewReused
    def getMethodType(): String = methodType
   
    override def toString(): String = {
        s"AdditionalPart($name, est=$estSize, act=$actSize, relativeSize=$relativeSize, estNumOfMethod=$estNumOfMethod, estIsNewReused=$estIsNewReused, actNumOfMethod=$actNumOfMethod, actIsNewReused=$actIsNewReused, type=$methodType)"
    }
}
