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
import models.{ErrorResponse, FailureMessage}
import play.api.Logging
import play.api.http.HeaderNames
import play.api.libs.json.*
import play.api.mvc.*
import play.api.mvc.Results.*

import scala.concurrent.*

class AuthenticatedAction @Inject() (val ec: ExecutionContext, val bodyParser: BodyParsers.Default)
    extends ActionBuilderImpl(bodyParser)(ec)
    with Logging {

  override def invokeBlock[A](request: Request[A], block: Request[A] => Future[Result]): Future[Result] = {
    logger.info(s"[AuthenticatedAction][invokeBlock] Received headers: ${request.headers}")

    request.headers.get(HeaderNames.AUTHORIZATION) match {
      case None    => Future.successful(Unauthorized(Json.toJson(ErrorResponse(List(FailureMessage.MissingBearerToken)))))
      case Some(_) => block(request)
    }
  }
}
