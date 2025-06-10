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
import config.HeaderKeys
import models.{ErrorResponse, FailureMessage}
import play.api.Logging
import play.api.libs.json.*
import play.api.mvc.*
import play.api.mvc.Results.*

import scala.concurrent.*

class CorrelationIdAction @Inject() (val ec: ExecutionContext, val bodyParser: BodyParsers.Default)
    extends ActionBuilderImpl(bodyParser)(ec)
    with Logging {

  private final lazy val correlationIdRegex =
    "^[0-9a-fA-F]{8}[-][0-9a-fA-F]{4}[-][0-9a-fA-F]{4}[-][0-9a-fA-F]{4}[-][0-9a-fA-F]{12}$"

  private def isValidCorrelationId(correlationId: String): Boolean =
    correlationId.matches(correlationIdRegex)

  override def invokeBlock[A](request: Request[A], block: Request[A] => Future[Result]): Future[Result] = {
    val maybeCorrelationId: Option[String] = request.headers.get(HeaderKeys.correlationId)
    logger.info(s"[CorrelationIdAction][invokeBlock] Received headers: ${request.headers}")

    maybeCorrelationId match {
      case Some(correlationId) if isValidCorrelationId(correlationId) =>
        logger.info("[CorrelationIdAction][invokeBlock] Valid correlation ID found: " + correlationId)
        block(request)
      case Some(correlationId)                                        =>
        logger.error(s"[CorrelationIdAction][invokeBlock] Invalid correlation ID format: $correlationId")
        Future.successful(BadRequest(Json.toJson(ErrorResponse(List(FailureMessage.InvalidCorrelationId)))))
      case _                                                          =>
        logger.error(s"[CorrelationIdAction][invokeBlock] No correlation ID found")
        // TODO update
        Future.successful(BadRequest(Json.toJson(ErrorResponse(List(FailureMessage.InvalidCorrelationId)))))
    }
  }
}
