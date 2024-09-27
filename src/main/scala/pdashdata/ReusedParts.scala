package pdashdata

class ReusedParts(
    val name: String,
    val estSize: Double,
    val actSize: Double,
) {
    override def toString: String = {
        s"ReusedParts(name=$name, estSize=$estSize, actSize=$actSize)"
    }
}
