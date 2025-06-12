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

class ErrorResponseSpec extends BaseSpec {

  "ErrorResponse" when {
    val hipFailure = HipFailureMessage(
      timestamp = "2025-06-09T12:00:00.000Z",
      error = "Bad Request",
      path = "/test/path",
      errors = Seq("Error 1", "Error 2")
    )

    val errorResponse = ErrorResponse("HIP", hipFailure)

    "creating an instance" should {
      "correctly set all fields" in {
        errorResponse.origin   shouldBe "HIP"
        errorResponse.response shouldBe hipFailure
      }
    }

    "converting to JSON" should {
      "correctly serialize using Json.toJson" in {
        val expectedJson = Json.obj(
          "origin"   -> "HIP",
          "response" -> Json.obj(
            "timestamp" -> "2025-06-09T12:00:00.000Z",
            "error"     -> "Bad Request",
            "path"      -> "/test/path",
            "errors"    -> Seq("Error 1", "Error 2")
          )
        )

        Json.toJson(errorResponse) shouldBe expectedJson
      }

      "correctly deserialize from JSON" in {
        val json = Json.obj(
          "origin"   -> "HIP",
          "response" -> Json.obj(
            "timestamp" -> "2025-06-09T12:00:00.000Z",
            "error"     -> "Bad Request",
            "path"      -> "/test/path",
            "errors"    -> Seq("Error 1", "Error 2")
          )
        )

        json.validate[ErrorResponse] shouldBe JsSuccess(errorResponse)
      }

      "fail to deserialize when origin is missing" in {
        val json = Json.obj(
          "response" -> Json.obj(
            "timestamp" -> "2025-06-09T12:00:00.000Z",
            "error"     -> "Bad Request",
            "path"      -> "/test/path",
            "errors"    -> Seq("Error 1", "Error 2")
          )
        )

        json.validate[ErrorResponse].isError shouldBe true
      }

      "fail to deserialize when response is missing" in {
        val json = Json.obj(
          "origin" -> "HIP"
        )

        json.validate[ErrorResponse].isError shouldBe true
      }

      "fail to deserialize with invalid response structure" in {
        val json = Json.obj(
          "origin"   -> "HIP",
          "response" -> Json.obj(
            "wrongField" -> "value"
          )
        )

        json.validate[ErrorResponse].isError shouldBe true
      }

      "fail to deserialize with wrong types" in {
        val json = Json.obj(
          "origin"   -> 123, // wrong type for origin
          "response" -> Json.obj(
            "timestamp" -> "2025-06-09T12:00:00.000Z",
            "error"     -> "Bad Request",
            "path"      -> "/test/path",
            "errors"    -> Seq("Error 1", "Error 2")
          )
        )

        json.validate[ErrorResponse].isError shouldBe true

        val json2 = Json.obj(
          "origin"   -> "HIP",
          "response" -> "not an object" // wrong type for response
        )

        json2.validate[ErrorResponse].isError shouldBe true
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
