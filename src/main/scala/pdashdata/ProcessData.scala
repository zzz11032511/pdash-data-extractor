package pdashdata

import scala.jdk.CollectionConverters._

class ProcessData(
    val timeLogs: List[TimeLog],     // 時間ログ
    val defectLogs: List[DefectLog], // 欠陥ログ
    val programDatas: Map[Int, ProgramData] // 課題ごとのデータ
) {
    def getTimeLogs(): java.util.List[TimeLog] = timeLogs.asJava
    def getDefectLogs(): java.util.List[DefectLog] = defectLogs.asJava
    def getProgramDatas(): java.util.Map[Int, ProgramData] = programDatas.asJava

    /**
     * 指定した課題までのフェーズごとの累積時間を返す
     *
     * @param from  開始する課題番号
     * @param to    終了する課題番号
     * @param phase フェーズ名
     * @return 累積時間
     */
    def cumulativeTimeBetween(from: Int, to: Int, phase: String): Double = {
        (from to to).map(i => programDatas(i).phaseDatas(phase).time).sum
    }

    /**
     * 指定した課題までの累積時間を返す
     *
     * @param from  開始する課題番号
     * @param to    終了する課題番号
     * @return 累積時間
     */
    def cumulativeTotalTimeBetween(from: Int, to: Int): Double = {
        (from to to).map(i => programDatas(i).totalTime).sum
    }

    /**
     * 指定した課題範囲におけるフェーズごとの埋め込まれた欠陥の累積値を返す
     *
     * @param from  開始する課題番号
     * @param to    終了する課題番号
     * @param phase フェーズ名
     * @return 指定範囲内で埋め込まれた欠陥の累積値
     */
    def cumulativeInjectedDefectsBetween(from: Int, to: Int, phase: String): Double = {
        (from to to).map(i => programDatas(i).phaseDatas(phase).injectedDefects).sum
    }

    /**
     * 指定した課題範囲におけるフェーズごとの除去した欠陥の累積値を返す
     *
     * @param from  開始する課題番号
     * @param to    終了する課題番号
     * @param phase フェーズ名
     * @return 指定範囲内で除去した欠陥の累積値
     */
    def cumulativeRemovedDefectsBetween(from: Int, to: Int, phase: String): Double = {
        (from to to).map(i => programDatas(i).phaseDatas(phase).removedDefects).sum
    }

    /**
     * 指定した課題範囲で発生した欠陥の累積値を返す
     *
     * @param from  開始する課題番号
     * @param to    終了する課題番号
     * @return 欠陥の累積値
     */
    def cumulativeTotalDefectsBetween(from: Int, to: Int): Double = {
        (from to to).map(i => programDatas(i).totalDefects).sum
    }
}
