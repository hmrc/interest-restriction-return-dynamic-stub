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

package controllers

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Result
import play.api.mvc.Results._
import play.api.test.Helpers._

import scala.concurrent.Future
import scala.io.{BufferedSource, Source}

class JsonSchemaHelperSpec extends AnyWordSpec with Matchers {

  private val source: BufferedSource   = Source.fromFile("conf/resources/examples/example_submit_full_irr_body.json")
  private val requestBodyJson: JsValue =
    try Json.parse(source.mkString)
    finally source.close()

  "JsonSchemaHelper" when {
    ".applySchemaValidation" should {
      "return 200 OK" when {
        "a valid schema path is found" in {
          val result: Future[Result] = JsonSchemaHelper.applySchemaValidation(
            schemaPath = "/resources/schemas/submit_full_irr.json",
            jsonBody = Some(requestBodyJson)
          )(Future.successful(Ok))

          status(result) shouldBe OK
        }
      }

      "return 500 INTERNAL_SERVER_ERROR" when {
        "an invalid schema path is found" in {
          val result: Future[Result] = JsonSchemaHelper.applySchemaValidation(
            schemaPath = "/resources/schemas/submit_full_irr_incorrect.json",
            jsonBody = Some(requestBodyJson)
          )(Future.successful(Ok))

          status(result) shouldBe INTERNAL_SERVER_ERROR
        }
      }
    }
  }
}
