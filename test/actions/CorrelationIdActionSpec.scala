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
import config.HeaderKeys
import models.{ErrorResponse, FailureMessage}
import play.api.libs.json.Json
import play.api.mvc.Results.Ok
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.Future

class CorrelationIdActionSpec extends BaseSpec {

  lazy val target = new CorrelationIdAction(ec, bodyParsers)

  "CorrelationIdAction" when {
    "a request is received" should {
      "proceed with the request when a valid correlation ID is present" in {
        val validCorrelationId = "8006795e-af82-488d-bb04-a0e2d64916be"
        val request            = FakeRequest().withHeaders(HeaderKeys.correlationId -> validCorrelationId)

        val result = target.invokeBlock(request, _ => Future.successful(Ok))

        status(result) shouldBe OK
      }

      "return BAD_REQUEST when correlation ID is invalid" in {
        val invalidCorrelationId = "invalid-correlation-id"
        val request              = FakeRequest().withHeaders(HeaderKeys.correlationId -> invalidCorrelationId)

        val result = target.invokeBlock(request, _ => Future.successful(Ok))

        status(result)        shouldBe BAD_REQUEST
        contentAsJson(result) shouldBe Json.toJson(ErrorResponse(List(FailureMessage.InvalidCorrelationId)))
      }

      "return BAD_REQUEST when correlation ID is missing" in {
        val request = FakeRequest()

        val result = target.invokeBlock(request, _ => Future.successful(Ok))

        status(result)        shouldBe BAD_REQUEST
        contentAsJson(result) shouldBe Json.toJson(ErrorResponse(List(FailureMessage.InvalidCorrelationId)))
      }
    }
  }
}
