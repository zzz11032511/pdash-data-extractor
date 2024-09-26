import sbtassembly.MergeStrategy

ThisBuild / version := "0.1.0"

ThisBuild / scalaVersion := "3.3.3"

lazy val root = (project in file("."))
  .settings(
    name := "pdash-data-extractor"
  )

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest-flatspec" % "3.2.19" % "test",
  "org.scalatest" %% "scalatest-diagrams" % "3.2.19" % "test",
)

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
  case PathList("META-INF", xs @ _*) => MergeStrategy.first
  case "reference.conf" => MergeStrategy.concat
  case "src/main/scala/pdashdata/Main.scala" => MergeStrategy.discard
  case _ => MergeStrategy.first
}
