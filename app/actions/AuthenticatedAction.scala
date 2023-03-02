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

package actions

import com.google.inject.Inject
import play.api.mvc.Results._
import play.api.mvc._
import play.api.http.HeaderNames

import scala.concurrent._
import scala.concurrent.Future
import models.{ErrorResponse, FailureMessage}
import play.api.libs.json._

class AuthenticatedAction @Inject() (override val parser: BodyParsers.Default)(implicit
  override val executionContext: ExecutionContext
) extends ActionBuilderImpl(parser) {

  override def invokeBlock[A](request: Request[A], block: Request[A] => Future[Result]): Future[Result] =
    request.headers.get(HeaderNames.AUTHORIZATION) match {
      case None    => Future.successful(Unauthorized(Json.toJson(ErrorResponse(List(FailureMessage.MissingBearerToken)))))
      case Some(_) => block(request)
    }
}
