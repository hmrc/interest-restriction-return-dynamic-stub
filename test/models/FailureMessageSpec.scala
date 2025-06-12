/*
 * Copyright 2025 HM Revenue & Customs
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

import base.BaseSpec
import play.api.libs.json.*

class FailureMessageSpec extends BaseSpec {

  val failureMessages: List[FailureMessage] =
    List(
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

  "FailureMessageSpec" should {
    "serialise to Json" in {
      Json.toJson(failureMessages.head) shouldBe json("failures").head.get
    }

    "to deserialise from Json" in {
      json("failures").as[List[FailureMessage]].head shouldBe failureMessages.head
    }

    "error when JSON is empty" in {
      JsObject.empty.validate[FailureMessage] shouldBe a[JsError]
    }

    "error when JSON has invalid types" in {
      Json.obj("code" -> JsNumber(1), "reason" -> JsBoolean(true)).validate[FailureMessage] shouldBe a[JsError]
    }

    "error when JSON has invalid types2" in {
      Json.arr(JsBoolean(true)).validate[FailureMessage] shouldBe a[JsError]
    }
  }
}
