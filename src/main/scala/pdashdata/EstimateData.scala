package pdashdata

class EstimateData(
    val value: Double,
    val probeMethod: String,
    val beta0: Double,
    val beta1: Double,
    val intervalPercent: Double,
    val r2: Double,
    val range: Double,
    val lpi: Double,
    val upi: Double
) {
    def getValue(): Double = value
    def getProbeMethod(): String = probeMethod
    def getBeta0(): Double = beta0
    def getBeta1(): Double = beta1
    def getIntervalPercent(): Double = intervalPercent
    def getR2(): Double = r2
    def getRange(): Double = range
    def getLPI(): Double = lpi
    def getUPI(): Double = upi
    
    override def toString(): String = {
        s"EstimateData($value, PROBE $probeMethod, beta0=$beta0, beta1=$beta1, intervalPercent=$intervalPercent, r2=$r2, range=$range, LPI=$lpi, UPI=$upi)"
    }
}
