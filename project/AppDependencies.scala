import sbt.*

object AppDependencies {

  val bootstrapVersion = "9.1.0"

  private lazy val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"                  %% "bootstrap-backend-play-30" % bootstrapVersion,
    "com.networknt"                 % "json-schema-validator"     % "1.5.1",
    "com.fasterxml.jackson.module" %% "jackson-module-scala"      % "2.17.2"
  )

  private lazy val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc" %% "bootstrap-test-play-30" % bootstrapVersion
  ).map(_ % Test)

  def apply(): Seq[ModuleID]           = compile ++ test

}
