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
import play.api.libs.json.{JsNull, JsValue, Json}

class IRRResponseSpec extends BaseSpec {

  "IRRResponse" when {
    "creating a new instance" should {
      "generate a random UUID as acknowledgementReference when using apply()" in {
        val response1 = IRRResponse()
        val response2 = IRRResponse()

        response1.acknowledgementReference should not be empty
        response2.acknowledgementReference should not be empty
        response1.acknowledgementReference should not be response2.acknowledgementReference
      }

      "use provided acknowledgementReference when using explicit constructor" in {
        val ref      = "test-reference"
        val response = IRRResponse(ref)

        response.acknowledgementReference shouldBe ref
      }
    }

    "converting to JSON" should {
      "correctly serialize using toJson" in {
        val ref          = "test-reference"
        val response     = IRRResponse(ref)
        val expectedJson = Json.obj("acknowledgementReference" -> ref)

        response.toJson shouldBe expectedJson
      }

      "correctly serialize using Json.toJson" in {
        val ref          = "test-reference"
        val response     = IRRResponse(ref)
        val expectedJson = Json.obj("acknowledgementReference" -> ref)

        Json.toJson(response) shouldBe expectedJson
      }

      "correctly deserialize from JSON" in {
        val ref  = "test-reference"
        val json = Json.obj("acknowledgementReference" -> ref)

        json.as[IRRResponse] shouldBe IRRResponse(ref)
      }

      "fail to deserialize from invalid JSON" in {
        val json = Json.obj("wrongField" -> "test-reference")

        json.validate[IRRResponse].isError shouldBe true
      }
    }

    "using case class features" should {
      "implement toString correctly" in {
        val ref      = "test-reference"
        val response = IRRResponse(ref)
        response.toString should include(ref)
      }

      "implement equals correctly" in {
        val ref       = "test-reference"
        val response1 = IRRResponse(ref)
        val response2 = IRRResponse(ref)
        val response3 = IRRResponse("different-reference")

        response1 shouldBe response2
        response1   should not be response3
        response1 shouldBe response1 // reflexive equality
      }

      "implement hashCode correctly" in {
        val ref       = "test-reference"
        val response1 = IRRResponse(ref)
        val response2 = IRRResponse(ref)

        response1.hashCode shouldBe response2.hashCode
      }

      "work correctly in collections" in {
        val ref      = "test-reference"
        val response = IRRResponse(ref)
        val set      = Set(response, response)

        set.size shouldBe 1 // due to proper equals/hashCode
      }
    }

    "handling edge cases" should {
      "accept empty acknowledgementReference" in {
        val response = IRRResponse("")
        val json     = Json.toJson(response)
        json.as[IRRResponse].acknowledgementReference shouldBe empty
      }

      "fail to deserialize null acknowledgementReference" in {
        val json = Json.obj("acknowledgementReference" -> JsNull)
        json.validate[IRRResponse].isError shouldBe true
      }

      "fail to deserialize when acknowledgementReference is missing" in {
        val json = Json.obj()
        json.validate[IRRResponse].isError shouldBe true
      }

      "fail to deserialize when acknowledgementReference is missing2" in {
        val json = Json.arr()
        json.validate[IRRResponse].isError shouldBe true
      }
    }
  }
}
