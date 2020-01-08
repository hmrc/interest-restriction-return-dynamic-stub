import play.core.PlayVersion.current
import play.sbt.PlayImport._
import sbt.Keys.libraryDependencies
import sbt._

object AppDependencies {

  val compile = Seq(
    "uk.gov.hmrc"             %% "simple-reactivemongo"         % "7.22.0-play-26",
    "uk.gov.hmrc"             %% "bootstrap-play-26"            % "1.3.0",
    "com.github.fge"          %  "json-schema-validator"        % "2.2.6"
  )

  val test = Seq(
    "uk.gov.hmrc"             %% "hmrctest"                     % "3.9.0-play-26"         % "test",
    "uk.gov.hmrc"             %% "bootstrap-play-26"            % "1.3.0"                 % Test classifier "tests",
    "org.scalatest"           %% "scalatest"                    % "3.0.8"                 % "test",
    "com.typesafe.play"       %% "play-test"                    % current                 % "test",
    "org.pegdown"             %  "pegdown"                      % "1.6.0"                 % "test",
    "org.scalatestplus.play"  %% "scalatestplus-play"           % "3.1.2"                 % "test",
    "org.mockito"             %  "mockito-all"                  % "1.10.19"               % "test",
    "com.github.fge"          %  "json-schema-validator"        % "2.2.6"                 % "test",
    "org.scalamock"           %% "scalamock-scalatest-support"  % "3.6.0"                 % "test",
    "com.github.fge"          %  "json-schema-validator"        % "2.2.6"                 % "test"
  )

  def tmpMacWorkaround(): Seq[ModuleID] =
    if (sys.props.get("os.name").exists(_.toLowerCase.contains("mac")))
      Seq("org.reactivemongo" % "reactivemongo-shaded-native" % "0.17.1-osx-x86-64" % "runtime,test")
    else Seq()

  def apply() = compile ++ test ++ tmpMacWorkaround

}
