val appName = "interest-restriction-return-dynamic-stub"

ThisBuild / scalaVersion := "3.4.2"
ThisBuild / majorVersion := 0

lazy val microservice = Project(appName, file("."))
  .enablePlugins(PlayScala, SbtDistributablesPlugin)
  .settings(
    libraryDependencies ++= AppDependencies(),
    PlayKeys.playDefaultPort := 9262,
    scalacOptions := scalacOptions.value.diff(Seq("-Wunused:all"))
  )
  .settings(CodeCoverageSettings.settings)

addCommandAlias("scalafmtAll", "all scalafmtSbt scalafmt Test/scalafmt")
