import play.core.PlayVersion.current
import sbt._

object AppDependencies {

  val bootstrapVersion = "7.14.0"

  private lazy val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"   %% "bootstrap-backend-play-28" % bootstrapVersion,
    "com.github.fge" % "json-schema-validator"     % "2.2.14"
  )

  private lazy val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"            %% "bootstrap-test-play-28" % bootstrapVersion,
    "org.scalatest"          %% "scalatest"              % "3.2.15",
    "com.typesafe.play"      %% "play-test"              % "2.8.19",
    "com.vladsch.flexmark"    % "flexmark-all"           % "0.62.2",
    "org.scalatestplus.play" %% "scalatestplus-play"     % "5.1.0",
    "com.github.tomakehurst"  % "wiremock-standalone"    % "2.27.2"
  ).map(_ % Test)

  def apply(): Seq[ModuleID]           = compile ++ test

}
