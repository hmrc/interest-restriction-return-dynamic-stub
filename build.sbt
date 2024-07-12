val appName = "interest-restriction-return-dynamic-stub"

ThisBuild / scalaVersion := "2.13.14"
ThisBuild / majorVersion := 0

lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)
  .settings(
    libraryDependencies ++= AppDependencies(),
    PlayKeys.playDefaultPort := 9262,
    scalacOptions ++= Seq("-feature", "-Wconf:src=routes/.*:s")
  )
  .settings(CodeCoverageSettings.settings)

addCommandAlias("scalafmtAll", "all scalafmtSbt scalafmt Test/scalafmt")
