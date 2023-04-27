val appName = "interest-restriction-return-dynamic-stub"

lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)
  .settings(
    // To resolve a bug with version 2.x.x of the scoverage plugin - https://github.com/sbt/sbt/issues/6997
    libraryDependencySchemes += "org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always,
    majorVersion := 0,
    scalaVersion := "2.13.10",
    libraryDependencies ++= AppDependencies(),
    PlayKeys.playDefaultPort := 9262,
    scalacOptions ++= Seq("-feature", "-Wconf:src=routes/.*:s")
  )
  .settings(CodeCoverageSettings.settings)

addCommandAlias("scalafmtAll", "all scalafmtSbt scalafmt Test/scalafmt")
addCommandAlias("scalastyleAll", "all scalastyle Test/scalastyle")
