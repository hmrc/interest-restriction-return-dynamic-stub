ThisBuild / scalaVersion := "3.5.2"
ThisBuild / majorVersion := 0

lazy val microservice = Project("interest-restriction-return-dynamic-stub", file("."))
  .enablePlugins(PlayScala, SbtDistributablesPlugin)
  .settings(
    libraryDependencies ++= AppDependencies(),
    PlayKeys.playDefaultPort := 9262,
    scalacOptions := Seq("-feature")
  )
  .settings(CodeCoverageSettings())

addCommandAlias("scalafmtAll", "all scalafmtSbt scalafmt Test/scalafmt")
