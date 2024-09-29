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
    override def toString(): String = {
        s"EstimateData(value=$value, probeMethod=$probeMethod, beta0=$beta0, beta1=$beta1, intervalPercent=$intervalPercent, r2=$r2, range=$range, lpi=$lpi, upi=$upi)"
    }
}
