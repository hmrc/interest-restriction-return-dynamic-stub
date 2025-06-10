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
import models.{ErrorResponse, FailureMessage}
import play.api.http.HeaderNames
import play.api.libs.json.Json
import play.api.mvc.Results.Ok
import play.api.test.FakeRequest
import play.api.test.Helpers.*

import scala.concurrent.Future

class AuthenticatedActionSpec extends BaseSpec {

  lazy val target = new AuthenticatedAction(ec, bodyParsers)

  "AuthenticatedAction" when {
    "a request is received" should {
      "return Unauthorized when no bearer token is present" in {
        val request = FakeRequest()

        val result = target.invokeBlock(request, _ => Future.successful(Ok))

        status(result)        shouldBe UNAUTHORIZED
        contentAsJson(result) shouldBe Json.toJson(ErrorResponse(List(FailureMessage.MissingBearerToken)))
      }

      "proceed with the request when a bearer token is present" in {
        val request = FakeRequest().withHeaders(HeaderNames.AUTHORIZATION -> "Bearer token123")

        val result = target.invokeBlock(request, _ => Future.successful(Ok))

        status(result) shouldBe OK
      }

    }
  }
}
