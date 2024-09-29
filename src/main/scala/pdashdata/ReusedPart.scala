package pdashdata

class ReusedPart(
    val name: String,
    val estSize: Double,
    val actSize: Double,
) {
    override def toString: String = {
        s"ReusedPart(name=$name, estSize=$estSize, actSize=$actSize)"
    }
}
