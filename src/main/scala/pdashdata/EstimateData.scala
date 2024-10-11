package pdashdata

class EstimateData(
    val value: Double, // 見積り結果
    val probeMethod: String, // 選択したPROBEの手法
    val beta0: Double,       // beta0(切片)
    val beta1: Double,       // beta1(傾き)
    val intervalPercent: Double,  // 予測区間
    val r2: Double,    // 相対規模の2乗
    val range: Double, // 予測区間の範囲
    val lpi: Double,   // LPI(value - range)
    val upi: Double    // UPI(value + range)
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
        s"EstimateData: $value, PROBE $probeMethod, beta0=$beta0, beta1=$beta1, intervalPercent=$intervalPercent, r2=$r2, range=$range, LPI=$lpi, UPI=$upi)"
    }
}
