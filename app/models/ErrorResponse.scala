/*
 * Copyright 2024 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package models

import play.api.libs.json.*

case class ErrorResponse(failures: List[FailureMessage])

case class FailureMessage(code: String, reason: String)

object ErrorResponse {
  given OFormat[ErrorResponse] = Json.format[ErrorResponse]
}

object FailureMessage {
  given OFormat[FailureMessage] = Json.format[FailureMessage]

  val InvalidJson: FailureMessage          =
    FailureMessage("INVALID_PAYLOAD", "Submission has not passed validation. Invalid payload.")
  val ServerError: FailureMessage          =
    FailureMessage("SERVER_ERROR", "IF is currently experiencing problems that require live service intervention.")
  val ServiceUnavailable: FailureMessage   =
    FailureMessage("SERVICE_UNAVAILABLE", "Dependent systems are currently not responding.")
  val Unauthorized: FailureMessage         = FailureMessage("UNAUTHORIZED", "Request Unauthorized.")
  val InvalidCorrelationId: FailureMessage =
    FailureMessage("INVALID_CORRELATIONID", "Submission has not passed validation. Invalid Header CorrelationId.")
  val MissingBearerToken: FailureMessage   = FailureMessage("MISSING_BEARER_TOKEN", "Bearer token is missing.")
  val MissingBody: FailureMessage          = FailureMessage("MISSING_BODY", "There was no body provided.")
  val InvalidEnvironment: FailureMessage   = FailureMessage("INVALID_ENVIRONMENT", "The environment is invalid.")
}
