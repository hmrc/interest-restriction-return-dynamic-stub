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

package models.cir

import base.BaseSpec
import play.api.libs.json.{JsSuccess, Json}

class HipFailureMessageSpec extends BaseSpec {

  "HipFailureMessage" when {
    val timestamp = "2025-06-09T12:00:00.000Z"
    val error     = "Bad Request"
    val path      = "/test/path"
    val errors    = Seq("Error 1", "Error 2")

    val hipFailure = HipFailureMessage(
      timestamp = timestamp,
      error = error,
      path = path,
      errors = errors
    )

    "creating an instance" should {
      "correctly set all fields" in {
        hipFailure.timestamp shouldBe timestamp
        hipFailure.error     shouldBe error
        hipFailure.path      shouldBe path
        hipFailure.errors    shouldBe errors
      }
    }

    "converting to JSON" should {
      "correctly serialize using Json.toJson" in {
        val expectedJson = Json.obj(
          "timestamp" -> timestamp,
          "error"     -> error,
          "path"      -> path,
          "errors"    -> errors
        )

        Json.toJson(hipFailure) shouldBe expectedJson
      }

      "correctly deserialize from JSON" in {
        val json = Json.obj(
          "timestamp" -> timestamp,
          "error"     -> error,
          "path"      -> path,
          "errors"    -> errors
        )

        json.validate[HipFailureMessage] shouldBe JsSuccess(hipFailure)
      }

      "fail to deserialize when required fields are missing" in {
        val jsonMissingTimestamp = Json.obj(
          "error"  -> error,
          "path"   -> path,
          "errors" -> errors
        )

        jsonMissingTimestamp.validate[HipFailureMessage].isError shouldBe true

        val jsonMissingError = Json.obj(
          "timestamp" -> timestamp,
          "path"      -> path,
          "errors"    -> errors
        )

        jsonMissingError.validate[HipFailureMessage].isError shouldBe true

        val jsonMissingPath = Json.obj(
          "timestamp" -> timestamp,
          "error"     -> error,
          "errors"    -> errors
        )

        jsonMissingPath.validate[HipFailureMessage].isError shouldBe true

        val jsonMissingErrors = Json.obj(
          "timestamp" -> timestamp,
          "error"     -> error,
          "path"      -> path
        )

        jsonMissingErrors.validate[HipFailureMessage].isError shouldBe true
      }

      "fail to deserialize with invalid types" in {
        val jsonInvalidTypes = Json.obj(
          "timestamp" -> 123,
          "error"     -> true,
          "path"      -> Json.obj(),
          "errors"    -> "not an array"
        )

        jsonInvalidTypes.validate[HipFailureMessage].isError shouldBe true
      }

      "correctly handle empty errors array" in {
        val hipFailureEmptyErrors = hipFailure.copy(errors = Seq.empty)
        val json                  = Json.obj(
          "timestamp" -> timestamp,
          "error"     -> error,
          "path"      -> path,
          "errors"    -> Seq.empty[String]
        )

        json.validate[HipFailureMessage] shouldBe JsSuccess(hipFailureEmptyErrors)
        Json.toJson(hipFailureEmptyErrors) shouldBe json
      }
    }
  }
}
