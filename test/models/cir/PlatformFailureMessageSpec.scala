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
import play.api.libs.json.{JsNull, JsSuccess, Json, OFormat}

class PlatformFailureMessageSpec extends BaseSpec {

  "PlatformFailureMessage" when {
    val failureType   = "INVALID_PAYLOAD"
    val failureReason = "Invalid request payload"
    val message       = PlatformFailureMessage(failureType, failureReason)

    "creating an instance" should {
      "correctly set all fields" in {
        message.`type` shouldBe failureType
        message.reason shouldBe failureReason
      }

      "handle special characters in type and reason" in {
        val specialMessage = PlatformFailureMessage(
          `type` = "ERROR_WITH_SPECIAL_CHARS_£$%^",
          reason = "Error message with special chars: £$%^&*()_+"
        )

        specialMessage.`type` shouldBe "ERROR_WITH_SPECIAL_CHARS_£$%^"
        specialMessage.reason shouldBe "Error message with special chars: £$%^&*()_+"
      }
    }

    "converting to JSON" should {
      "correctly serialize using Json.toJson" in {
        val expectedJson = Json.obj(
          "type"   -> failureType,
          "reason" -> failureReason
        )

        Json.toJson(message) shouldBe expectedJson
      }

      "correctly serialize with special characters" in {
        val specialMessage = PlatformFailureMessage(
          `type` = "ERROR_WITH_SPECIAL_CHARS_£$%^",
          reason = "Error message with special chars: £$%^&*()_+"
        )

        val expectedJson = Json.obj(
          "type"   -> "ERROR_WITH_SPECIAL_CHARS_£$%^",
          "reason" -> "Error message with special chars: £$%^&*()_+"
        )

        Json.toJson(specialMessage) shouldBe expectedJson
      }

      "correctly deserialize from JSON" in {
        val json = Json.obj(
          "type"   -> failureType,
          "reason" -> failureReason
        )

        json.validate[PlatformFailureMessage] shouldBe JsSuccess(message)
      }

      "fail to deserialize when type is missing" in {
        val json = Json.obj(
          "reason" -> failureReason
        )

        json.validate[PlatformFailureMessage].isError shouldBe true
      }

      "fail to deserialize when reason is missing" in {
        val json = Json.obj(
          "type" -> failureType
        )

        json.validate[PlatformFailureMessage].isError shouldBe true
      }

      "fail to deserialize with wrong types" in {
        val jsonWithWrongTypes = Json.obj(
          "type"   -> 123,
          "reason" -> true
        )

        jsonWithWrongTypes.validate[PlatformFailureMessage].isError shouldBe true
      }

      "fail to deserialize with null values" in {
        val jsonWithNulls = Json.obj(
          "type"   -> JsNull,
          "reason" -> JsNull
        )

        jsonWithNulls.validate[PlatformFailureMessage].isError shouldBe true
      }
    }

    "handling edge cases" should {
      "accept empty strings" in {
        val message = PlatformFailureMessage("", "")
        val json    = Json.toJson(message)
        json.as[PlatformFailureMessage] shouldBe message
      }

      "handle special characters" in {
        val message = PlatformFailureMessage(
          "TYPE_WITH_SPECIAL_£$%^&*",
          "Reason with special chars: £$%^&* and unicode: 你好"
        )
        val json    = Json.toJson(message)
        json.as[PlatformFailureMessage] shouldBe message
      }

      "handle very long strings" in {
        val longString = "a" * 10000
        val message    = PlatformFailureMessage(longString, longString)
        val json       = Json.toJson(message)
        json.as[PlatformFailureMessage] shouldBe message
      }
    }

    "Format instance" should {
      "be available implicitly" in {
        summon[OFormat[PlatformFailureMessage]] shouldBe PlatformFailureMessage.given_OFormat_PlatformFailureMessage
      }

      "use correct field names in JSON" in {
        val json    = Json.obj(
          "type"   -> "TEST_TYPE",
          "reason" -> "Test reason"
        )
        val message = PlatformFailureMessage("TEST_TYPE", "Test reason")

        Json.toJson(message)            shouldBe json
        json.as[PlatformFailureMessage] shouldBe message
      }
    }

    "case class features" should {
      "handle equality correctly" in {
        val message1 = PlatformFailureMessage("TYPE1", "Reason1")
        val message2 = PlatformFailureMessage("TYPE1", "Reason1")
        val message3 = PlatformFailureMessage("TYPE1", "Different")

        message1 shouldBe message2
        message1   should not be message3
      }

      "implement hashCode correctly" in {
        val message1 = PlatformFailureMessage("TYPE1", "Reason1")
        val message2 = PlatformFailureMessage("TYPE1", "Reason1")
        val set      = Set(message1, message2)

        set.size shouldBe 1 // due to proper hashCode implementation
      }

      "provide meaningful toString" in {
        val message = PlatformFailureMessage("TEST_TYPE", "Test reason")
        val str     = message.toString
        str should include("TEST_TYPE")
        str should include("Test reason")
      }
    }
  }
}
