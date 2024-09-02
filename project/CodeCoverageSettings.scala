import sbt.Setting
import scoverage.ScoverageKeys.{coverageExcludedFiles, coverageFailOnMinimum, coverageHighlighting, coverageMinimumStmtTotal}

object CodeCoverageSettings {

  val settings: Seq[Setting[?]] = Seq(
    coverageExcludedFiles := ".*Routes.*",
    coverageMinimumStmtTotal := 98,
    coverageFailOnMinimum := true,
    coverageHighlighting := true
  )
}
