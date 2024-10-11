package pdashdata

class AdditionalPart(
    val name: String,    // 部品の名前
    val estSize: Double, // 計画規模
    val actSize: Double, // 実績規模
    val relativeSize: String,    // 相対規模
    val estNumOfMethod: Int,     // 計画メソッド数
    val estIsNewReused: Boolean, // 計画時点で新規再利用部品かどうか
    val actNumOfMethod: Int,     // 実績メソッド数
    val actIsNewReused: Boolean, // 新規再利用部品かどうか
    val methodType: String,      // メソッドの種類
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
        s"AdditionalPart: $name, est=$estSize, act=$actSize, relativeSize=$relativeSize, estNumOfMethod=$estNumOfMethod, estIsNewReused=$estIsNewReused, actNumOfMethod=$actNumOfMethod, actIsNewReused=$actIsNewReused, type=$methodType)"
    }
}
