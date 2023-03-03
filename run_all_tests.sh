#!/usr/bin/env bash

sbt -Dlogger.resource=logback-test.xml scalafmtAll scalastyleAll clean compile coverage Test/test coverageOff dependencyUpdates coverageReport
