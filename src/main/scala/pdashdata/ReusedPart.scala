package pdashdata

class ReusedPart(
    val name: String,
    val estSize: Double,
    val actSize: Double,
) {
    def getName(): String = name
    def getEstSize(): Double = estSize
    def getActSize(): Double = actSize
    
    override def toString: String = {
        s"ReusedPart($name, est=$estSize, act=$actSize)"
    }
}
