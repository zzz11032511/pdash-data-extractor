package pdashdata

class BasePart(
    val name: String,  // 部品の名前
    val estBase: Double,     // 計画時点でのベース規模
    val estAdded: Double,    // 計画時点でのベースへの追加規模
    val estModified: Double, // 計画時点でのベースの変更規模
    val estDeleted: Double,  // 計画時点でのベースの削除規模
    val actBase: Double,     // 実績のベース規模
    val actAdded: Double,    // 実績のベースへの追加規模
    val actModified: Double, // 実績のベースの変更規模
    val actDeleted: Double,  // 実績のベースの削除規模
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
        s"BasePart: $name, estBase=$estBase, estAdded=$estAdded, estModified=$estModified, estDeleted=$estDeleted, actBase=$actBase, actAdded=$actAdded, actModified=$actModified, actDeleted=$actDeleted)"
    }
}
