import play.core.PlayVersion.current
import sbt._

object AppDependencies {

  private lazy val silencerVersion = "1.7.9"

  private lazy val silencerDependencies: Seq[ModuleID] = Seq(
    compilerPlugin("com.github.ghik" % "silencer-plugin" % silencerVersion cross CrossVersion.full),
    "com.github.ghik" % "silencer-lib" % silencerVersion % Provided cross CrossVersion.full
  )

  private lazy val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"   %% "bootstrap-backend-play-28" % "6.4.0",
    "com.github.fge" % "json-schema-validator"     % "2.2.14"
  )

  private lazy val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"            %% "bootstrap-test-play-28" % "6.4.0",
    "org.scalatest"          %% "scalatest"              % "3.2.13",
    "com.typesafe.play"      %% "play-test"              % current,
    "com.vladsch.flexmark"    % "flexmark-all"           % "0.62.2",
    "org.scalatestplus.play" %% "scalatestplus-play"     % "5.1.0",
    "com.github.tomakehurst"  % "wiremock-standalone"    % "2.27.2"
  ).map(_ % Test)

  def apply(): Seq[ModuleID]           = compile ++ silencerDependencies ++ test

}
