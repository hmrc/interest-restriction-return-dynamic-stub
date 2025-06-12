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

import com.google.inject.Inject
import config.{EnvironmentValues, HeaderKeys}
import models.{ErrorResponse, FailureMessage}
import play.api.Logging
import play.api.libs.json.*
import play.api.mvc.*
import play.api.mvc.Results.*

import scala.concurrent.*

class EnvironmentAction @Inject() (val ec: ExecutionContext, val bodyParser: BodyParsers.Default)
    extends ActionBuilderImpl(bodyParser)(ec)
    with Logging {

  private def isValidEnvironment(environment: String): Boolean =
    EnvironmentValues.all.contains(environment)

  override def invokeBlock[A](request: Request[A], block: Request[A] => Future[Result]): Future[Result] = {
    val maybeEnvironment: Option[String] = request.headers.get(HeaderKeys.environment)
    logger.info(s"[EnvironmentAction][invokeBlock] Received headers: ${request.headers}")

    maybeEnvironment match {
      case Some(environment) if isValidEnvironment(environment)  =>
        logger.info("[EnvironmentAction][invokeBlock] Valid environment found: " + environment)
        block(request)
      case Some(environment) if !isValidEnvironment(environment) =>
        logger.error(s"[EnvironmentAction][invokeBlock] Invalid environment: $environment")
        Future.successful(BadRequest(Json.toJson(ErrorResponse(List(FailureMessage.InvalidEnvironment)))))
      case _                                                     =>
        logger.warn("[EnvironmentAction][invokeBlock] No environment header found")
        block(request)
    }
  }
}
