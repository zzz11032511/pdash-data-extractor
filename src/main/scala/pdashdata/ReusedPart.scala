package pdashdata

class ReusedPart(
    val name: String,   // 部品の名前
    val estSize: Double, // 計画規模
    val actSize: Double, // 実績規模
) {
    def getName(): String = name
    def getEstSize(): Double = estSize
    def getActSize(): Double = actSize
    
    override def toString(): String = {
        s"ReusedPart($name, est=$estSize, act=$actSize)"
    }
}
