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

package controllers.cir

import base.BaseSpec
import config.HeaderKeys
import file.FileReader.readFileAsJson
import models.IRRResponse
import models.cir.ErrorMessages
import play.api.http.HeaderNames
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers.*

import java.util.UUID
import scala.concurrent.Future

class AppointControllerSpec extends BaseSpec {

  private val exampleJsonBody: JsValue =
    readFileAsJson("conf/resources/cir/examples/example_appoint_reporting_company_body.json")

  private val controller = new AppointController(
    authenticatedAction,
    correlationIdAction,
    stubControllerComponents()
  )

  private val correlationId    = UUID.randomUUID().toString
  private val validRequestJson = exampleJsonBody

  def performRequest(json: JsValue, headers: Seq[(String, String)] = Seq()): Future[Result] = {
    val request = FakeRequest()
      .withMethod("POST")
      .withHeaders(
        (HeaderNames.AUTHORIZATION, "Bearer token") +:
          (HeaderKeys.correlationId, correlationId) +:
          headers*
      )
      .withJsonBody(json)

    controller.call()(request)
  }

  "AppointController" when {
    "call() is called" should {
      "return OK with IRRResponse for valid request" in {
        val result = performRequest(validRequestJson)

        status(result)                                               shouldBe OK
        contentAsJson(result).as[IRRResponse].acknowledgementReference should not be empty
        headers(result).get(HeaderKeys.correlationId)                shouldBe Some(correlationId)
      }

      "return INTERNAL_SERVER_ERROR when agentName is 'ServerError'" in {
        val json = Json.obj(
          "agentDetails" -> Json.obj(
            "agentActingOnBehalfOfCompany" -> true,
            "agentName"                    -> "ServerError"
          )
        )

        val result = performRequest(json)

        status(result)                                shouldBe INTERNAL_SERVER_ERROR
        contentAsJson(result)                         shouldBe ErrorMessages.INTERNAL_SERVER_ERROR.asJson
        headers(result).get(HeaderKeys.correlationId) shouldBe Some(correlationId)
      }

      "return SERVICE_UNAVAILABLE when agentName is 'ServiceUnavailable'" in {
        val json = Json.obj(
          "agentDetails" -> Json.obj(
            "agentActingOnBehalfOfCompany" -> true,
            "agentName"                    -> "ServiceUnavailable"
          )
        )

        val result = performRequest(json)

        status(result)                                shouldBe SERVICE_UNAVAILABLE
        contentAsJson(result)                         shouldBe ErrorMessages.SERVER_UNAVAILABLE.asJson
        headers(result).get(HeaderKeys.correlationId) shouldBe Some(correlationId)
      }

      "return BAD_REQUEST when request JSON is invalid according to schema" in {
        val invalidJson = Json.obj(
          "agentDetails" -> Json.obj(
            "agentActingOnBehalfOfCompany" -> "not a boolean" // should be boolean
          )
        )

        val result = performRequest(invalidJson)

        status(result) shouldBe BAD_REQUEST
      }

      "return BAD_REQUEST when request has no JSON body" in {
        val request = FakeRequest()
          .withMethod("POST")
          .withHeaders(
            HeaderNames.AUTHORIZATION -> "Bearer token",
            HeaderKeys.correlationId  -> correlationId
          )

        val result = controller.call()(request)

        status(result) shouldBe BAD_REQUEST
      }

      "handle missing correlation ID" in {
        val request = FakeRequest()
          .withMethod("POST")
          .withHeaders(HeaderNames.AUTHORIZATION -> "Bearer token")
          .withJsonBody(validRequestJson)
          .withMethod("POST")

        val result = controller.call()(request)

        status(result) shouldBe BAD_REQUEST
      }
    }
  }
}
