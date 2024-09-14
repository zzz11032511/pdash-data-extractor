import java.util.zip.ZipInputStream

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

object ParserUtils {
    def zipInputStreamToByteArrayInputStream(zipInputStream: ZipInputStream): ByteArrayInputStream = {
        val buffer = new ByteArrayOutputStream()
        val data = new Array[Byte](1024)

        var count = zipInputStream.read(data)
        while (count != -1) {
            buffer.write(data, 0, count)
            count = zipInputStream.read(data)
        }
    
        new ByteArrayInputStream(buffer.toByteArray)
    }
}
