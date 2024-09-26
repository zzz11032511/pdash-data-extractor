package pdashdata

import java.util.Date

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.zip.ZipInputStream

object ParserUtils {
    /**
     * ZipInputStreamをByteArrayInputStreamに変換する
     * 
     * ZipInputStreamからそのままデータを読み込むと、何故か勝手にcloseされてしまうため、
     * この関数でStreamを変換してからデータの読み込み処理を行う
     * 
     * @param zipInputStream ZipInputStream
     * @return ByteArrayInputStream
     */
    private[pdashdata] def zipInputToByteArrayInput(zipInputStream: ZipInputStream): ByteArrayInputStream = {
        val buffer = new ByteArrayOutputStream()
        val data = new Array[Byte](1024)

        var count = zipInputStream.read(data)
        while (count != -1) {
            buffer.write(data, 0, count)
            count = zipInputStream.read(data)
        }
    
        new ByteArrayInputStream(buffer.toByteArray)
    }

    /**
     * UNIX時間を表す文字列をDateオブジェクトに変換する
     * "@"で始まる文字列にも対応
     * 
     * @param timeStr UNIX時間を表す文字列
     * @return Dateオブジェクト
     */
    private[pdashdata] def timeStrToDate(timeStr: String): Date = {
        if (timeStr.charAt(0) == '@') {
            return new Date(timeStr.substring(1).toLong)
        }
        new Date(timeStr.toLong)
    }
}
