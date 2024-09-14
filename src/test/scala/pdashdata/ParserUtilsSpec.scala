import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.diagrams.Diagrams

import java.util.Date
import java.time.LocalDate

class ParserUtilsSpec extends AnyFlatSpec with Diagrams {
    "timeStrToDate" should "parse time string" in {
        val date1 = ParserUtils.timeStrToDate("1715589337682")
        assert(date1.getTime() == 1715589337682L)

        val date2 = ParserUtils.timeStrToDate("@1721882391466")
        assert(date2.getTime() == 1721882391466L)
    }
}
