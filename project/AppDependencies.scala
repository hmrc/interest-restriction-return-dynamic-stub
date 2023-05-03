import play.core.PlayVersion.current
import sbt._

object AppDependencies {

  val bootstrapVersion = "7.15.0"

  private lazy val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"   %% "bootstrap-backend-play-28" % bootstrapVersion,
    "com.github.fge" % "json-schema-validator"     % "2.2.14"
  )

  private lazy val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"           %% "bootstrap-test-play-28" % bootstrapVersion,
    "org.scalatest"         %% "scalatest"              % "3.2.15",
    "com.typesafe.play"     %% "play-test"              % current,
    "com.vladsch.flexmark"   % "flexmark-all"           % "0.64.4",
    "com.github.tomakehurst" % "wiremock-standalone"    % "2.27.2"
  ).map(_ % Test)

  def apply(): Seq[ModuleID]           = compile ++ test

}
