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

package actions

import base.BaseSpec
import config.{EnvironmentValues, HeaderKeys}
import models.{ErrorResponse, FailureMessage}
import play.api.libs.json.Json
import play.api.mvc.Results.Ok
import play.api.test.FakeRequest
import play.api.test.Helpers.*

import scala.concurrent.Future

class EnvironmentActionSpec extends BaseSpec {

  lazy val target = new EnvironmentAction(ec, bodyParsers)

  "EnvironmentAction" when {
    "a request is received" should {
      "proceed with the request when a valid environment is present" in {
        val request = FakeRequest().withHeaders(HeaderKeys.environment -> EnvironmentValues.environmentDev)

        val result = target.invokeBlock(request, _ => Future.successful(Ok))

        status(result) shouldBe OK
      }

      "return BAD_REQUEST when environment is invalid" in {
        val request = FakeRequest().withHeaders(HeaderKeys.environment -> "invalid-environment")

        val result = target.invokeBlock(request, _ => Future.successful(Ok))

        status(result)        shouldBe BAD_REQUEST
        contentAsJson(result) shouldBe Json.toJson(ErrorResponse(List(FailureMessage.InvalidEnvironment)))
      }

      "return OK when environment header is missing" in {
        val request = FakeRequest()
        val result  = target.invokeBlock(request, _ => Future.successful(Ok))

        status(result) shouldBe OK
      }

      "check all valid environment values" in {
        val results = EnvironmentValues.all.map { env =>
          val request = FakeRequest().withHeaders(HeaderKeys.environment -> env)
          target.invokeBlock(request, _ => Future.successful(Ok))
        }

        results.foreach { result =>
          status(result) shouldBe OK
        }
      }

    }
  }
}
