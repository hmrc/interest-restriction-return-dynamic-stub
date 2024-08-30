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

  val json: JsObject =
    Json.obj(
      "failures" -> List(
        Json.obj(
          "code"   -> "INVALID_PAYLOAD",
          "reason" -> "Submission has not passed validation. Invalid payload."
        )
      )
    )

  val model =
    ErrorResponse(
      List(FailureMessage("INVALID_PAYLOAD", "Submission has not passed validation. Invalid payload."))
    )

  "ErrorResponse" should {

    "to serialise to Json" in {
      Json.toJson(model) shouldBe json
    }

    "to de-serialise to an ErrorResponse" in {

      json.as[ErrorResponse] shouldBe model
    }

    "error when JSON is invalid" in {
      JsObject.empty.validate[ErrorResponse] shouldBe a[JsError]
    }
  }
}
