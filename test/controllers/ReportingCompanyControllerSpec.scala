/*
 * Copyright 2023 HM Revenue & Customs
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
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.{HeaderNames, Status}
import play.api.libs.json.{JsObject, JsString, JsValue, Json}
import play.api.test.{FakeRequest, Helpers}
import play.api.test.Helpers._

import scala.io.{BufferedSource, Source}
import actions.AuthenticatedAction
import play.api.mvc.{AnyContentAsEmpty, BodyParsers}
import models.{ErrorResponse, FailureMessage}
import config._

import scala.util.Try
import java.util.UUID

class ReportingCompanyControllerSpec extends AnyWordSpec with Matchers with GuiceOneAppPerSuite {

  val sourceAppJoint: BufferedSource  =
    Source.fromFile("conf/resources/examples/example_appoint_irr_reporting_company_body.json")
  val exampleAppointJsonBody: JsValue =
    try Json.parse(sourceAppJoint.mkString)
    finally sourceAppJoint.close()

  val sourceRevoke: BufferedSource                                =
    Source.fromFile("conf/resources/examples/example_revoke_irr_reporting_company_body.json")
  val exampleRevokeJsonBody: JsValue                              =
    try Json.parse(sourceRevoke.mkString)
    finally sourceAppJoint.close()
  val FakeRequestWithHeaders: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest("POST", "/").withHeaders(HeaderNames.AUTHORIZATION -> "Bearer 1234")

  implicit val ec: scala.concurrent.ExecutionContext              = scala.concurrent.ExecutionContext.global
  val bodyParsers: BodyParsers.Default                            = app.injector.instanceOf[BodyParsers.Default]
  val authenticatedAction: AuthenticatedAction                    = new AuthenticatedAction(bodyParsers)

  "POST appoint irr reporting company" should {
    "return 201 when the payload is validated" in {
      val fakeRequest = FakeRequestWithHeaders.withJsonBody(exampleAppointJsonBody)
      val controller  = new ReportingCompanyController(authenticatedAction, Helpers.stubControllerComponents())

      val result = controller.appoint()(fakeRequest)

      status(result) shouldBe Status.CREATED
    }

    "return 201 when the payload is validated and the environment is valid" in {
      val env         = EnvironmentValues.environmentDev
      val fakeRequest =
        FakeRequestWithHeaders.withJsonBody(exampleAppointJsonBody).withHeaders(HeaderKeys.environment -> env)
      val controller  = new ReportingCompanyController(authenticatedAction, Helpers.stubControllerComponents())
      val result      = controller.appoint()(fakeRequest)
      status(result) shouldBe Status.CREATED
    }

    "return 400 when the payload is validated and the environment is valid" in {
      val env         = "Invalid environment"
      val fakeRequest =
        FakeRequestWithHeaders.withJsonBody(exampleAppointJsonBody).withHeaders(HeaderKeys.environment -> env)
      val controller  = new ReportingCompanyController(authenticatedAction, Helpers.stubControllerComponents())
      val result      = controller.appoint()(fakeRequest)
      status(result)                                        shouldBe Status.BAD_REQUEST
      contentAsJson(result).as[ErrorResponse].failures.head shouldBe FailureMessage.InvalidEnvironment
    }

    "return 201 and a correlationId when the payload is validated and the correlationId exists" in {
      val uuid        = java.util.UUID.randomUUID().toString
      val fakeRequest =
        FakeRequestWithHeaders.withJsonBody(exampleAppointJsonBody).withHeaders(HeaderKeys.correlationId -> uuid)
      val controller  = new ReportingCompanyController(authenticatedAction, Helpers.stubControllerComponents())
      val result      = controller.appoint()(fakeRequest)
      status(result)                           shouldBe Status.CREATED
      headers(result) contains HeaderKeys.correlationId
      header(HeaderKeys.correlationId, result) shouldBe Some(uuid)
    }

    "return 400 when the correlationId doesn't match the schema" in {
      val uuid        = "Not matching schema"
      val fakeRequest =
        FakeRequestWithHeaders.withJsonBody(exampleAppointJsonBody).withHeaders(HeaderKeys.correlationId -> uuid)
      val controller  = new ReportingCompanyController(authenticatedAction, Helpers.stubControllerComponents())
      val result      = controller.appoint()(fakeRequest)
      status(result)                                        shouldBe Status.BAD_REQUEST
      contentAsJson(result).as[ErrorResponse].failures.head shouldBe FailureMessage.InvalidCorrelationId
    }

    "returns 400 when the payload is invalid" in {
      val exampleInvalidJsonBody = exampleAppointJsonBody.as[JsObject] - "agentDetails"
      val fakeRequest            = FakeRequestWithHeaders.withJsonBody(exampleInvalidJsonBody)
      val controller             = new ReportingCompanyController(authenticatedAction, Helpers.stubControllerComponents())

      val result = controller.appoint()(fakeRequest)

      status(result)                                        shouldBe Status.BAD_REQUEST
      contentAsJson(result).as[ErrorResponse].failures.head shouldBe FailureMessage.InvalidJson
    }

    "returns a body containing acknowledgementReference when the payload is validated" in {
      val fakeRequest = FakeRequestWithHeaders.withJsonBody(exampleAppointJsonBody)
      val controller  = new ReportingCompanyController(authenticatedAction, Helpers.stubControllerComponents())

      val result = controller.appoint()(fakeRequest)

      Try((contentAsJson(result) \ "acknowledgementReference").as[UUID]) should be a Symbol("success")
    }

    "returns a 500 when a ServerError agent name is passed" in {
      val amendedBody = changeAgentName(exampleAppointJsonBody, Some("ServerError"))
      val fakeRequest = FakeRequestWithHeaders.withJsonBody(amendedBody)
      val controller  = new ReportingCompanyController(authenticatedAction, Helpers.stubControllerComponents())

      val result = controller.appoint()(fakeRequest)
      status(result)                                        shouldBe Status.INTERNAL_SERVER_ERROR
      contentAsJson(result).as[ErrorResponse].failures.head shouldBe FailureMessage.ServerError
    }

    "returns 503 when a Service unavailable agent name is passed" in {
      val amendedBody = changeAgentName(exampleAppointJsonBody, Some("ServiceUnavailable"))
      val fakeRequest = FakeRequestWithHeaders.withJsonBody(amendedBody)
      val controller  = new ReportingCompanyController(authenticatedAction, Helpers.stubControllerComponents())

      val result = controller.appoint()(fakeRequest)
      status(result)                                        shouldBe Status.SERVICE_UNAVAILABLE
      contentAsJson(result).as[ErrorResponse].failures.head shouldBe FailureMessage.ServiceUnavailable
    }

    "returns 401 when an Unauthorized agent name is passed" in {
      val amendedBody = changeAgentName(exampleAppointJsonBody, Some("Unauthorized"))
      val fakeRequest = FakeRequestWithHeaders.withJsonBody(amendedBody)
      val controller  = new ReportingCompanyController(authenticatedAction, Helpers.stubControllerComponents())

      val result = controller.appoint()(fakeRequest)
      status(result)                                        shouldBe Status.UNAUTHORIZED
      contentAsJson(result).as[ErrorResponse].failures.head shouldBe FailureMessage.Unauthorized
    }

    "returns 201 when a bearer token is passed" in {
      val fakeRequest = FakeRequestWithHeaders.withJsonBody(exampleAppointJsonBody)
      val controller  = new ReportingCompanyController(authenticatedAction, Helpers.stubControllerComponents())

      val result = controller.appoint()(fakeRequest)
      status(result) shouldBe Status.CREATED
    }

    "returns 401 when a bearer token is not passed" in {
      val fakeRequest = FakeRequest("POST", "/").withJsonBody(exampleAppointJsonBody)
      val controller  = new ReportingCompanyController(authenticatedAction, Helpers.stubControllerComponents())

      val result = controller.appoint()(fakeRequest)
      status(result)                                        shouldBe Status.UNAUTHORIZED
      contentAsJson(result).as[ErrorResponse].failures.head shouldBe FailureMessage.MissingBearerToken
    }

    "returns 400 when a body is empty" in {
      val fakeRequest = FakeRequestWithHeaders
      val controller  = new ReportingCompanyController(authenticatedAction, Helpers.stubControllerComponents())

      val result = controller.appoint()(fakeRequest)
      status(result)                                        shouldBe Status.BAD_REQUEST
      contentAsJson(result).as[ErrorResponse].failures.head shouldBe FailureMessage.MissingBody
    }

    "returns 201 when no agent name is passed" in {
      val amendedBody = changeAgentName(exampleAppointJsonBody, None)
      val fakeRequest = FakeRequestWithHeaders.withJsonBody(amendedBody)
      val controller  = new ReportingCompanyController(authenticatedAction, Helpers.stubControllerComponents())

      val result = controller.appoint()(fakeRequest)
      status(result) shouldBe Status.CREATED
    }
  }

  "POST revoke reporting company" should {
    "return 201 when the payload is validated" in {
      val fakeRequest = FakeRequestWithHeaders.withJsonBody(exampleRevokeJsonBody)
      val controller  = new ReportingCompanyController(authenticatedAction, Helpers.stubControllerComponents())

      val result = controller.revoke()(fakeRequest)

      status(result) shouldBe Status.CREATED
    }

    "return 201 when the payload is validated and the environment is valid" in {
      val env         = EnvironmentValues.environmentDev
      val fakeRequest =
        FakeRequestWithHeaders.withJsonBody(exampleRevokeJsonBody).withHeaders(HeaderKeys.environment -> env)
      val controller  = new ReportingCompanyController(authenticatedAction, Helpers.stubControllerComponents())
      val result      = controller.revoke()(fakeRequest)
      status(result) shouldBe Status.CREATED
    }

    "return 400 when the payload is validated and the environment is valid" in {
      val env         = "Invalid environment"
      val fakeRequest =
        FakeRequestWithHeaders.withJsonBody(exampleRevokeJsonBody).withHeaders(HeaderKeys.environment -> env)
      val controller  = new ReportingCompanyController(authenticatedAction, Helpers.stubControllerComponents())
      val result      = controller.revoke()(fakeRequest)
      status(result)                                        shouldBe Status.BAD_REQUEST
      contentAsJson(result).as[ErrorResponse].failures.head shouldBe FailureMessage.InvalidEnvironment
    }

    "return 201 and a correlationId when the payload is validated and the correlationId exists" in {
      val uuid        = java.util.UUID.randomUUID().toString
      val fakeRequest =
        FakeRequestWithHeaders.withJsonBody(exampleRevokeJsonBody).withHeaders(HeaderKeys.correlationId -> uuid)
      val controller  = new ReportingCompanyController(authenticatedAction, Helpers.stubControllerComponents())
      val result      = controller.revoke()(fakeRequest)
      status(result)                           shouldBe Status.CREATED
      headers(result) contains HeaderKeys.correlationId
      header(HeaderKeys.correlationId, result) shouldBe Some(uuid)
    }

    "return 400 when the correlationId doesn't match the schema" in {
      val uuid        = "Not matching schema"
      val fakeRequest =
        FakeRequestWithHeaders.withJsonBody(exampleRevokeJsonBody).withHeaders(HeaderKeys.correlationId -> uuid)
      val controller  = new ReportingCompanyController(authenticatedAction, Helpers.stubControllerComponents())
      val result      = controller.revoke()(fakeRequest)
      status(result)                                        shouldBe Status.BAD_REQUEST
      contentAsJson(result).as[ErrorResponse].failures.head shouldBe FailureMessage.InvalidCorrelationId
    }

    "returns 400 when the payload is invalid" in {
      val exampleInvalidJsonBody = exampleRevokeJsonBody.as[JsObject] - "agentDetails"
      val fakeRequest            = FakeRequestWithHeaders.withJsonBody(exampleInvalidJsonBody)
      val controller             = new ReportingCompanyController(authenticatedAction, Helpers.stubControllerComponents())

      val result = controller.revoke()(fakeRequest)

      status(result)                                        shouldBe Status.BAD_REQUEST
      contentAsJson(result).as[ErrorResponse].failures.head shouldBe FailureMessage.InvalidJson
    }

    "returns a body containing acknowledgementReference when the payload is validated" in {
      val fakeRequest = FakeRequestWithHeaders.withJsonBody(exampleRevokeJsonBody)
      val controller  = new ReportingCompanyController(authenticatedAction, Helpers.stubControllerComponents())

      val result = controller.revoke()(fakeRequest)

      Try((contentAsJson(result) \ "acknowledgementReference").as[UUID]) should be a Symbol("success")
    }

    "returns a 500 when a ServerError agent name is passed" in {
      val amendedBody = changeAgentName(exampleRevokeJsonBody, Some("ServerError"))
      val fakeRequest = FakeRequestWithHeaders.withJsonBody(amendedBody)
      val controller  = new ReportingCompanyController(authenticatedAction, Helpers.stubControllerComponents())

      val result = controller.revoke()(fakeRequest)
      status(result)                                        shouldBe Status.INTERNAL_SERVER_ERROR
      contentAsJson(result).as[ErrorResponse].failures.head shouldBe FailureMessage.ServerError
    }

    "returns 503 when a Service unavailable agent name is passed" in {
      val amendedBody = changeAgentName(exampleRevokeJsonBody, Some("ServiceUnavailable"))
      val fakeRequest = FakeRequestWithHeaders.withJsonBody(amendedBody)
      val controller  = new ReportingCompanyController(authenticatedAction, Helpers.stubControllerComponents())

      val result = controller.revoke()(fakeRequest)
      status(result)                                        shouldBe Status.SERVICE_UNAVAILABLE
      contentAsJson(result).as[ErrorResponse].failures.head shouldBe FailureMessage.ServiceUnavailable
    }

    "returns 401 when an Unauthorized agent name is passed" in {
      val amendedBody = changeAgentName(exampleRevokeJsonBody, Some("Unauthorized"))
      val fakeRequest = FakeRequestWithHeaders.withJsonBody(amendedBody)
      val controller  = new ReportingCompanyController(authenticatedAction, Helpers.stubControllerComponents())

      val result = controller.revoke()(fakeRequest)
      status(result)                                        shouldBe Status.UNAUTHORIZED
      contentAsJson(result).as[ErrorResponse].failures.head shouldBe FailureMessage.Unauthorized
    }

    "returns 201 when a bearer token is passed" in {
      val fakeRequest = FakeRequestWithHeaders.withJsonBody(exampleRevokeJsonBody)
      val controller  = new ReportingCompanyController(authenticatedAction, Helpers.stubControllerComponents())

      val result = controller.revoke()(fakeRequest)
      status(result) shouldBe Status.CREATED
    }

    "returns 401 when a bearer token is not passed" in {
      val fakeRequest = FakeRequest("POST", "/").withJsonBody(exampleRevokeJsonBody)
      val controller  = new ReportingCompanyController(authenticatedAction, Helpers.stubControllerComponents())

      val result = controller.revoke()(fakeRequest)
      status(result)                                        shouldBe Status.UNAUTHORIZED
      contentAsJson(result).as[ErrorResponse].failures.head shouldBe FailureMessage.MissingBearerToken
    }

    "returns 400 when a body is empty" in {
      val fakeRequest = FakeRequestWithHeaders
      val controller  = new ReportingCompanyController(authenticatedAction, Helpers.stubControllerComponents())

      val result = controller.revoke()(fakeRequest)
      status(result)                                        shouldBe Status.BAD_REQUEST
      contentAsJson(result).as[ErrorResponse].failures.head shouldBe FailureMessage.MissingBody
    }

    "returns 201 when no agent name is passed" in {
      val amendedBody = changeAgentName(exampleRevokeJsonBody, None)
      val fakeRequest = FakeRequestWithHeaders.withJsonBody(amendedBody)
      val controller  = new ReportingCompanyController(authenticatedAction, Helpers.stubControllerComponents())

      val result = controller.revoke()(fakeRequest)
      status(result) shouldBe Status.CREATED
    }
  }

  def changeAgentName(body: JsValue, newAgentName: Option[String]): JsObject = {
    val agentDetails        = body.as[JsObject] \ "agentDetails"
    val amendedAgentDetails = newAgentName match {
      case Some(name) => agentDetails.as[JsObject] + ("agentName" -> JsString(name))
      case None       => agentDetails.as[JsObject] - "agentName"
    }
    body.as[JsObject] + ("agentDetails" -> amendedAgentDetails)
  }
}
