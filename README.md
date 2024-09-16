# pdash-data-extractor

Process Dashboardのプロセスデータを解析し、データを抽出するためのライブラリ。
Releaseのダウンロードページから、jarファイルをダウンロードしてください。

## コンパイル・実行方法

### bash, cmd
```bash
javac -cp pdashDataExtractor.jar Foo.java
java -cp .:pdashDataExtractor.jar Foo
```

### powershell
```powershell
javac -cp ".;pdashDataExtractor.jar" Foo.java
java -cp ".;pdashDataExtractor.jar" Foo
```

## サンプル

### 時間記録ログの取得

PDashDataExtractorクラスのextractメソッドを使用して、プロセスデータの解析を行います。解析結果はProcessDataクラスのインスタンスとして取得できます。
ProcessDataクラスのgetTimeLogsメソッドを使用して、全課題の時間記録ログを取得します。TimeLogsはList\<TimeLog>型で、TimeLogクラスのインスタンスを要素として持ちます。

```java
import pdashdata.PDashDataExtractor;
import pdashdata.ProcessData;
import pdashdata.TimeLog;

public class Foo {
    public static void main(String[] args) {
        PDashDataExtractor pde = new PDashDataExtractor();
        ProcessData pd = pde.extract("pdash-John_Lennon-2024-01-01.zip");
        for (TimeLog timelog : pd.getTimeLogs()) {
            System.out.println(timelog);
        }
    }
}
```

TimeLogクラスのインスタンスは、以下の値を持ちます。
- int ID : 時間記録ログのID
- String program : 課題名("Program 1"など)
- String phase : フェーズ名("Planning"など)
- Date startTime : 開始時刻
- double delta : 経過時間
- double interrupt : 中断時間
- String comment : コメント

### 欠陥ログの取得

ProcessDataクラスのgetDefectLogsメソッドを使用して、全課題の欠陥ログを取得します。DefectLogsはMap\<Object, List\<DefectLog>>型で、Object型のキーに課題IDを、List\<DefectLog>型の値に欠陥ログのリストを持ちます。

```java
import pdashdata.PDashDataExtractor;
import pdashdata.ProcessData;
import pdashdata.DefectLog;

import java.util.List;
import java.util.Map;

public class Foo {
    public static void main(String[] args) {
        PDashDataExtractor pde = new PDashDataExtractor();
        ProcessData pd = pde.extract("pdash-John_Lennon-2024-01-01.zip");
        
        Map<Object, List<DefectLog>> defectLogs = pd.getDefectLogs();
        for (DefectLog dl : defectLogs.get(1)) {
            System.out.println(dl.toString());
        }
    }
}
```

DefectLogクラスのインスタンスは、以下の値を持ちます。
- int ID : 欠陥ログのID
- String defectType : 欠陥の種類("Syntax"など)
- String injected : 欠陥の注入フェーズ("Design"など)
- String removed : 欠陥の除去フェーズ("Compile"など)
- double fixTime : 修正時間
- int fixDefectID : 修正欠陥
- String description : 説明
- Date injectedDate : 欠陥の注入日時

## 注意

jarファイルのサイズが大きいのは、scalaの標準ライブラリを含んでいるためです。
