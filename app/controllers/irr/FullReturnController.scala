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

package controllers.irr

import actions.{AuthenticatedAction, CorrelationIdAction, EnvironmentAction}
import config.HeaderKeys
import controllers.JsonSchemaHelper
import models.{ErrorResponse, FailureMessage}
import play.api.Logging
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.*
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import java.util.UUID.randomUUID
import javax.inject.{Inject, Singleton}
import scala.concurrent.*

@Singleton()
class FullReturnController @Inject() (
  authenticatedAction: AuthenticatedAction,
  correlationIdAction: CorrelationIdAction,
  environmentAction: EnvironmentAction,
  cc: ControllerComponents
) extends BackendController(cc)
    with Logging
    with IrrBaseController {

  given ExecutionContext = cc.executionContext

  def fullReturn(): Action[AnyContent] =
    (authenticatedAction andThen correlationIdAction andThen environmentAction).async { request =>
      given Request[AnyContent]     = request
      val jsonBody: Option[JsValue] = request.body.asJson

      JsonSchemaHelper.applySchemaValidation(schemaDir, "submit_full.json", jsonBody) {
        val agentName = jsonBody.flatMap(body => (body \ "agentDetails" \ "agentName").asOpt[String])

        val response = agentName match {
          case Some("ServerError")        => InternalServerError(Json.toJson(ErrorResponse(List(FailureMessage.ServerError))))
          case Some("ServiceUnavailable") =>
            ServiceUnavailable(Json.toJson(ErrorResponse(List(FailureMessage.ServiceUnavailable))))
          case Some("Unauthorized")       => Unauthorized(Json.toJson(ErrorResponse(List(FailureMessage.Unauthorized))))
          case _                          =>
            val acknowledgementReference = randomUUID().toString
            val responseString           = s"""{"acknowledgementReference":"$acknowledgementReference"}"""
            val responseJson             = Json.parse(responseString)
            Created(responseJson)
        }

        Future.successful(
          response.withHeaders(
            HeaderKeys.correlationId -> request.headers.get(HeaderKeys.correlationId).getOrElse(""),
            HeaderKeys.environment   -> request.headers.get(HeaderKeys.environment).getOrElse("")
          )
        )
      }

    }

}
