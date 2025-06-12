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

class ErrorResponseSpec extends BaseSpec {

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
  private val errorResponse: ErrorResponse  = ErrorResponse(failures = failureMessages)

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

  "ErrorResponseSpec" should {
    "serialise to Json" in {
      Json.toJson(errorResponse) shouldBe json
    }

    "to deserialise from Json" in {
      json.as[ErrorResponse] shouldBe errorResponse
    }

    "error when JSON is invalid" in {
      JsObject.empty.validate[ErrorResponse] shouldBe a[JsError]
    }

    "error when JSON has invalid types" in {
      Json.arr("failures" -> JsNumber(1)).validate[ErrorResponse] shouldBe a[JsError]
    }

    "error when JSON has invalid types2" in {
      Json.obj("key" -> JsBoolean(true)).validate[ErrorResponse] shouldBe a[JsError]
    }

  }

  "ErrorResponse" when {
    val failure1 = FailureMessage("TYPE1", "First error")
    val failure2 = FailureMessage("TYPE2", "Second error")

    "creating instances" should {
      "handle empty list of failures" in {
        val response = ErrorResponse(List.empty)
        response.failures shouldBe empty
      }

      "handle single failure" in {
        val response = ErrorResponse(List(failure1))
        response.failures shouldBe List(failure1)
      }

      "handle multiple failures" in {
        val response = ErrorResponse(List(failure1, failure2))
        response.failures should contain theSameElementsAs List(failure1, failure2)
      }
    }

    "JSON formatting" should {
      "correctly serialize using Json.toJson" in {
        val response     = ErrorResponse(List(failure1, failure2))
        val expectedJson = Json.obj(
          "failures" -> Json.arr(
            Json.obj("code" -> "TYPE1", "reason" -> "First error"),
            Json.obj("code" -> "TYPE2", "reason" -> "Second error")
          )
        )

        Json.toJson(response) shouldBe expectedJson
      }

      "correctly serialize empty list" in {
        val response     = ErrorResponse(List.empty)
        val expectedJson = Json.obj("failures" -> Json.arr())

        Json.toJson(response) shouldBe expectedJson
      }

      "correctly deserialize from JSON" in {
        val json = Json.obj(
          "failures" -> Json.arr(
            Json.obj("code" -> "TYPE1", "reason" -> "First error"),
            Json.obj("code" -> "TYPE2", "reason" -> "Second error")
          )
        )

        json.validate[ErrorResponse] shouldBe JsSuccess(ErrorResponse(List(failure1, failure2)))
      }

      "correctly deserialize empty array" in {
        val json = Json.obj("failures" -> Json.arr())
        json.validate[ErrorResponse] shouldBe JsSuccess(ErrorResponse(List.empty))
      }

      "fail to deserialize null failures" in {
        val json = Json.obj("failures" -> JsNull)
        json.validate[ErrorResponse].isError shouldBe true
      }

      "fail to deserialize when failures field is missing" in {
        val json = Json.obj()
        json.validate[ErrorResponse].isError shouldBe true
      }

      "fail to deserialize invalid failure messages" in {
        val json = Json.obj(
          "failures" -> Json.arr(
            Json.obj("invalid" -> "object")
          )
        )
        json.validate[ErrorResponse].isError shouldBe true
      }
    }

    "OFormat instance" should {
      "be available implicitly" in {
        summon[OFormat[ErrorResponse]] shouldBe ErrorResponse.given_OFormat_ErrorResponse
      }

      "handle round trip conversion" in {
        val original = ErrorResponse(List(failure1, failure2))
        val json     = Json.toJson(original)
        val parsed   = json.as[ErrorResponse]

        parsed shouldBe original
      }
    }

    "case class features" should {
      "implement equals correctly" in {
        val response1 = ErrorResponse(List(failure1, failure2))
        val response2 = ErrorResponse(List(failure1, failure2))
        val response3 = ErrorResponse(List(failure2, failure1)) // different order

        response1 shouldBe response2 // same content, same order
        response1   should not be response3 // same content, different order
      }

      "implement hashCode correctly" in {
        val response1 = ErrorResponse(List(failure1, failure2))
        val response2 = ErrorResponse(List(failure1, failure2))
        val set       = Set(response1, response2)

        set.size shouldBe 1 // due to correct hashCode implementation
      }

      "implement toString sensibly" in {
        val response = ErrorResponse(List(failure1))
        response.toString should include("TYPE1")
        response.toString should include("First error")
      }
    }

    "companion object" should {
      "support creating response with varargs" in {
        val response = ErrorResponse(Seq(failure1, failure2))
        response.failures shouldBe List(failure1, failure2)
      }

    }

    "handling edge cases" should {
      "handle large number of failures" in {
        val largeList = List.fill(1000)(failure1)
        val response  = ErrorResponse(largeList)
        response.failures                         should have size 1000
        Json.toJson(response).as[ErrorResponse] shouldBe response // verify JSON serialization works with large lists
      }

      "preserve duplicates in failure list" in {
        val duplicates = List(failure1, failure1, failure2)
        val response   = ErrorResponse(duplicates)
        response.failures should have size 3
        response.failures should contain theSameElementsInOrderAs duplicates
      }

      "maintain list order in equality checks" in {
        val response1 = ErrorResponse(List(failure1, failure2))
        val response2 = ErrorResponse(List(failure2, failure1))

        response1          should not be response2
        response1.failures should not be response2.failures
      }
    }
  }
}
