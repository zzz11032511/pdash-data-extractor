package pdashdata

class BasePart(
    val name: String,
    val estBase: Double,
    val estAdded: Double,
    val estModified: Double,
    val estDeleted: Double,
    val actBase: Double,
    val actAdded: Double,
    val actModified: Double,
    val actDeleted: Double,
) {
    override def toString(): String = {
        s"BasePart($name, estBase=$estBase, estAdded=$estAdded, estModified=$estModified, estDeleted=$estDeleted, actBase=$actBase, actAdded=$actAdded, actModified=$actModified, actDeleted=$actDeleted)"
    }
}
