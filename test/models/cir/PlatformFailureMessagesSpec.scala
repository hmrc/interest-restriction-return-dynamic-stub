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
import play.api.libs.json.*

class PlatformFailureMessagesSpec extends BaseSpec {

  "PlatformFailureMessages" when {
    val failure1 = PlatformFailureMessage("ERROR1", "First error message")
    val failure2 = PlatformFailureMessage("ERROR2", "Second error message")
    val messages = PlatformFailureMessages(Seq(failure1, failure2))

    "creating an instance" should {
      "correctly set failures sequence" in {
        messages.failures shouldBe Seq(failure1, failure2)
      }

      "handle empty failures sequence" in {
        val emptyMessages = PlatformFailureMessages(Seq.empty)
        emptyMessages.failures shouldBe empty
      }

      "handle single failure" in {
        val singleMessage = PlatformFailureMessages(Seq(failure1))
        singleMessage.failures shouldBe Seq(failure1)
      }
    }

    "converting to JSON" should {
      "correctly serialize using Json.toJson" in {
        val expectedJson = Json.obj(
          "failures" -> Json.arr(
            Json.obj(
              "type"   -> "ERROR1",
              "reason" -> "First error message"
            ),
            Json.obj(
              "type"   -> "ERROR2",
              "reason" -> "Second error message"
            )
          )
        )

        Json.toJson(messages) shouldBe expectedJson
      }

      "correctly serialize empty failures" in {
        val emptyMessages = PlatformFailureMessages(Seq.empty)
        val expectedJson  = Json.obj("failures" -> Json.arr())

        Json.toJson(emptyMessages) shouldBe expectedJson
      }

      "correctly deserialize from JSON" in {
        val json = Json.obj(
          "failures" -> Json.arr(
            Json.obj(
              "type"   -> "ERROR1",
              "reason" -> "First error message"
            ),
            Json.obj(
              "type"   -> "ERROR2",
              "reason" -> "Second error message"
            )
          )
        )

        json.validate[PlatformFailureMessages] shouldBe JsSuccess(messages)
      }

      "correctly deserialize empty array" in {
        val json = Json.obj("failures" -> Json.arr())

        json.validate[PlatformFailureMessages] shouldBe JsSuccess(PlatformFailureMessages(Seq.empty))
      }

      "fail to deserialize when failures field is missing" in {
        val json = Json.obj()

        json.validate[PlatformFailureMessages].isError shouldBe true
      }

      "fail to deserialize when failures is not an array" in {
        val json = Json.obj(
          "failures" -> "not an array"
        )

        json.validate[PlatformFailureMessages].isError shouldBe true
      }

      "fail to deserialize when array contains invalid failure messages" in {
        val json = Json.obj(
          "failures" -> Json.arr(
            Json.obj("invalid" -> "object"),
            Json.obj(
              "type"           -> "ERROR1",
              "reason"         -> "Valid message"
            )
          )
        )

        json.validate[PlatformFailureMessages].isError shouldBe true
      }
    }

    "handling edge cases" should {
      "handle empty failures sequence" in {
        val messages = PlatformFailureMessages(Seq.empty)
        val json     = Json.toJson(messages)
        json.as[PlatformFailureMessages] shouldBe messages
      }

      "handle large failures sequence" in {
        val largeSeq = (1 to 1000).map(i => PlatformFailureMessage(s"TYPE_$i", s"Reason $i"))
        val messages = PlatformFailureMessages(largeSeq)
        val json     = Json.toJson(messages)
        json.as[PlatformFailureMessages] shouldBe messages
      }

      "fail with null failures sequence" in {
        val json = Json.obj("failures" -> JsNull)
        json.validate[PlatformFailureMessages].isError shouldBe true
      }

      "fail to deserialize when failures is missing" in {
        val json = Json.obj()
        json.validate[PlatformFailureMessages].isError shouldBe true
      }

      "fail to deserialize when failures is missing2" in {
        val json = Json.arr()
        json.validate[PlatformFailureMessages].isError shouldBe true
      }
    }

  }
}
