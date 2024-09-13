import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.diagrams.Diagrams

class DataFileParserSpec extends AnyFlatSpec with Diagrams {
    val parser = new DataFileParser()

    "DataFileParser" should "parse a line with a key and value separated properly" in {
        var (key1, value1) = parser.parseLine("New Objects/0/Actual New Reused?=1.0")
        assert(key1.equals("New Objects/0/Actual New Reused?"))
        assert(value1.equals("1.0"))

        var (key2, value2) = parser.parseLine("Test/Time==10.0")
        assert(key2.equals("Test/Time"))
        assert(value2.equals("10.0"))

        var (key3, value3) = parser.parseLine("New Objects/0/Type=\"Logic")
        assert(key3.equals("New Objects/0/Type"))
        assert(value3.equals("Logic"))

        var (key4, value4) = parser.parseLine("New_Objects_List==\"012345")
        assert(key4.equals("New_Objects_List"))
        assert(value4.equals("012345"))
    }
}
