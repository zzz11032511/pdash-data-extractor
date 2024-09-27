package pdashdata

class BaseParts(
    val name: String,
    val estBase: Int,
    val estAdded: Int,
    val estModified: Int,
    val estDeleted: Int,
    val actBase: Int,
    val actAdded: Int,
    val actModified: Int,
    val actDeleted: Int,
) {
    override def toString(): String = {
        s"BaseParts(name=$name, estBase=$estBase, estAdded=$estAdded, estModified=$estModified, estDeleted=$estDeleted, actBase=$actBase, actAdded=$actAdded, actModified=$actModified, actDeleted=$actDeleted)"
    }
}
