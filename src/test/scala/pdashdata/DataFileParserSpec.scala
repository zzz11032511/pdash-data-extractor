import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.diagrams.Diagrams

class DataFileParserSpec extends AnyFlatSpec with Diagrams {
    val parser = new DataFileParser()

    "DataFileParser" should "parse a line with a key and value separated by '='" in {
        val (key, value) = parser.parseLine("New Objects/0/Actual New Reused?=1.0")
        assert(key.equals("New Objects/0/Actual New Reused?"))
        assert(value.equals("1.0"))
    }

    "DataFileParser" should "parse a line with a key and value separated by '=='" in {
        val (key, value) = parser.parseLine("Test/Time==10.0")
        assert(key.equals("Test/Time"))
        assert(value.equals("10.0"))
    }

    "DataFileParser" should "parse a line with a key and value separated by '=\"'" in {
        val (key, value) = parser.parseLine("New Objects/0/Type=\"Logic")
        assert(key.equals("New Objects/0/Type"))
        assert(value.equals("Logic"))
    }

    "DataFileParser" should "parse a line with a key and value separated by '==\"'" in {
        val (key, value) = parser.parseLine("Test/Type==\"Logic")
        assert(key.equals("Test/Type"))
        assert(value.equals("Logic"))
    }
}
