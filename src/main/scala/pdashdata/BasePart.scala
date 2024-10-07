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
    def getName(): String = name
    def getEstBase(): Double = estBase
    def getEstAdded(): Double = estAdded
    def getEstModified(): Double = estModified
    def getEstDeleted(): Double = estDeleted
    def getActBase(): Double = actBase
    def getActAdded(): Double = actAdded
    def getActModified(): Double = actModified
    def getActDeleted(): Double = actDeleted
    
    override def toString(): String = {
        s"BasePart($name, estBase=$estBase, estAdded=$estAdded, estModified=$estModified, estDeleted=$estDeleted, actBase=$actBase, actAdded=$actAdded, actModified=$actModified, actDeleted=$actDeleted)"
    }
}
