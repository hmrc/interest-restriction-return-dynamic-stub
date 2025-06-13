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

import actions.{AuthenticatedAction, CorrelationIdAction}
import config.HeaderKeys
import controllers.JsonSchemaHelper
import models.IRRResponse
import models.cir.ErrorMessages
import play.api.Logging
import play.api.libs.json.JsValue
import play.api.mvc.{Action, AnyContent, ControllerComponents, Request}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton()
class RevokeController @Inject() (
  authenticatedAction: AuthenticatedAction,
  correlationIdAction: CorrelationIdAction,
  cc: ControllerComponents
) extends BackendController(cc)
    with Logging
    with CirBaseController {

  given ExecutionContext = cc.executionContext

  def call(): Action[AnyContent] = (authenticatedAction andThen correlationIdAction).async { request =>
    given Request[AnyContent] = request

    val jsonBody: Option[JsValue] = request.body.asJson
    val agentName                 = jsonBody.flatMap(body => (body \ "agentDetails" \ "agentName").asOpt[String])

    val response = agentName match {
      case Some("ServerError")        => Future.successful(InternalServerError(ErrorMessages.INTERNAL_SERVER_ERROR.asJson))
      case Some("ServiceUnavailable") => Future.successful(ServiceUnavailable(ErrorMessages.SERVER_UNAVAILABLE.asJson))
      case _                          =>
        JsonSchemaHelper.applySchemaValidation(schemaDir, "revoke.json", jsonBody) {
          Future.successful(Created(IRRResponse().toJson))
        }
    }

    response.map(
      _.withHeaders(
        HeaderKeys.correlationId -> request.headers.get(HeaderKeys.correlationId).getOrElse("")
      )
    )
  }
}
