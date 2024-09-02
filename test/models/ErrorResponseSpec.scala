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

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.libs.json.{JsError, JsObject, Json}

class ErrorResponseSpec extends AnyWordSpec with Matchers with GuiceOneAppPerSuite {

  private val errorResponse: ErrorResponse = ErrorResponse(
    failures = List[FailureMessage](
      FailureMessage(
        code = "INVALID_PAYLOAD",
        reason = "Submission has not passed validation. Invalid payload."
      ),
      FailureMessage(
        code = "SERVER_ERROR",
        reason = "IF is currently experiencing problems that require live service intervention."
      ),
      FailureMessage(
        code = "SERVICE_UNAVAILABLE",
        reason = "Dependent systems are currently not responding."
      ),
      FailureMessage(
        code = "UNAUTHORIZED",
        reason = "Request Unauthorized."
      ),
      FailureMessage(
        code = "INVALID_CORRELATIONID",
        reason = "Submission has not passed validation. Invalid Header CorrelationId."
      ),
      FailureMessage(
        code = "MISSING_BEARER_TOKEN",
        reason = "Bearer token is missing."
      ),
      FailureMessage(
        code = "MISSING_BODY",
        reason = "There was no body provided."
      ),
      FailureMessage(
        code = "INVALID_ENVIRONMENT",
        reason = "The environment is invalid."
      )
    )
  )

  private val json =
    Json.obj(
      "failures" -> Json.arr(
        Json.obj("code" -> "INVALID_PAYLOAD", "reason"      -> "Submission has not passed validation. Invalid payload."),
        Json.obj(
          "code"        -> "SERVER_ERROR",
          "reason"      -> "IF is currently experiencing problems that require live service intervention."
        ),
        Json.obj("code" -> "SERVICE_UNAVAILABLE", "reason"  -> "Dependent systems are currently not responding."),
        Json.obj("code" -> "UNAUTHORIZED", "reason"         -> "Request Unauthorized."),
        Json.obj(
          "code"        -> "INVALID_CORRELATIONID",
          "reason"      -> "Submission has not passed validation. Invalid Header CorrelationId."
        ),
        Json.obj("code" -> "MISSING_BEARER_TOKEN", "reason" -> "Bearer token is missing."),
        Json.obj("code" -> "MISSING_BODY", "reason"         -> "There was no body provided."),
        Json.obj("code" -> "INVALID_ENVIRONMENT", "reason"  -> "The environment is invalid.")
      )
    )

  "ErrorResponse" should {
    "serialise to Json" in {
      Json.toJson(errorResponse) shouldBe json
    }

    "to deserialise from Json" in {
      json.as[ErrorResponse] shouldBe errorResponse
    }

    "error when JSON is invalid" in {
      JsObject.empty.validate[ErrorResponse] shouldBe a[JsError]
    }
  }

  "FailureMessage" should {
    "serialise to Json" in {
      Json.toJson(errorResponse.failures.head) shouldBe json("failures").head.get
    }

    "to deserialise from Json" in {
      json("failures").as[List[FailureMessage]].head shouldBe errorResponse.failures.head
    }

    "error when JSON is invalid" in {
      JsObject.empty.validate[FailureMessage] shouldBe a[JsError]
    }
  }
}
