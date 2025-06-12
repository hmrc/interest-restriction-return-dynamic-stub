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
import play.api.libs.json.{JsValue, Json}

class ErrorResponseHipSpec extends BaseSpec {

  private val failureMessage = PlatformFailureMessage(
    `type` = "INVALID_PAYLOAD",
    reason = "Invalid request payload"
  )

  private val failureMessages = PlatformFailureMessages(
    failures = Seq(failureMessage)
  )

  private val errorResponseHip = ErrorResponseHip(
    origin = "HIP",
    response = failureMessages
  )

  "ErrorResponseHip" when {
    "creating an instance" should {
      "correctly set all fields" in {
        errorResponseHip.origin                 shouldBe "HIP"
        errorResponseHip.response               shouldBe failureMessages
        errorResponseHip.response.failures.head shouldBe failureMessage
      }
    }

    "converting to JSON using asJson" should {
      "produce the correct JSON structure" in {
        val expectedJson = Json.obj(
          "origin"   -> "HIP",
          "response" -> Json.obj(
            "failures" -> Json.arr(
              Json.obj(
                "type"   -> "INVALID_PAYLOAD",
                "reason" -> "Invalid request payload"
              )
            )
          )
        )

        errorResponseHip.asJson shouldBe expectedJson
      }
    }

    "converting to/from JSON using Json.format" should {
      "correctly serialize using Json.toJson" in {
        val expectedJson = Json.obj(
          "origin"   -> "HIP",
          "response" -> Json.obj(
            "failures" -> Json.arr(
              Json.obj(
                "type"   -> "INVALID_PAYLOAD",
                "reason" -> "Invalid request payload"
              )
            )
          )
        )

        Json.toJson(errorResponseHip) shouldBe expectedJson
      }

      "correctly deserialize valid JSON" in {
        val json = Json.obj(
          "origin"   -> "HIP",
          "response" -> Json.obj(
            "failures" -> Json.arr(
              Json.obj(
                "type"   -> "INVALID_PAYLOAD",
                "reason" -> "Invalid request payload"
              )
            )
          )
        )

        json.as[ErrorResponseHip] shouldBe errorResponseHip
      }

      "fail to deserialize when origin is missing" in {
        val json = Json.obj(
          "response" -> Json.obj(
            "failures" -> Json.arr(
              Json.obj(
                "type"   -> "INVALID_PAYLOAD",
                "reason" -> "Invalid request payload"
              )
            )
          )
        )

        json.validate[ErrorResponseHip].isError shouldBe true
      }

      "fail to deserialize when response is missing" in {
        val json = Json.obj(
          "origin" -> "HIP"
        )

        json.validate[ErrorResponseHip].isError shouldBe true
      }

      "deserialize when failures array is empty" in {
        val json = Json.obj(
          "origin"   -> "HIP",
          "response" -> Json.obj(
            "failures" -> Json.arr()
          )
        )

        json.validate[ErrorResponseHip].isError shouldBe false
      }

      "handle multiple failure messages" in {
        val multipleFailures = PlatformFailureMessages(
          failures = Seq(
            PlatformFailureMessage("ERROR1", "First error"),
            PlatformFailureMessage("ERROR2", "Second error")
          )
        )

        val response = ErrorResponseHip("HIP", multipleFailures)
        val json     = Json.toJson(response)

        json.as[ErrorResponseHip] shouldBe response
      }
    }

    "handling edge cases" should {

      "fail to deserialize when origin and response is missing" in {
        val json = Json.obj()
        json.validate[ErrorResponse].isError shouldBe true
      }

      "fail to deserialize when origin and response is missing2" in {
        val json = Json.arr()
        json.validate[ErrorResponse].isError shouldBe true
      }
    }
  }
}
