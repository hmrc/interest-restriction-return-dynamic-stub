/*
 * Copyright 2019 HM Revenue & Customs
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

package controllers.stub

import controllers.BaseController
import javax.inject.{Inject, Singleton}
import models.HttpMethod._
import models.{DataIdModel, DataModel}
import play.api.mvc._
import repositories.DataRepository
import utils.{MongoSugar, SchemaValidation}

import scala.concurrent.Future

@Singleton
class RequestHandlerController @Inject()(schemaValidation: SchemaValidation,
                                                 dataRepository: DataRepository,
                                                 override val controllerComponents: ControllerComponents) extends BaseController with MongoSugar {

  def getRequestHandler(url: String): Action[AnyContent] = Action.async { implicit request =>
    findById(dataRepository)(DataIdModel(request.uri, GET)) { data =>
      Future.successful(returnResponse(data))
    }
  }

  def postRequestHandler(url: String): Action[AnyContent] = requestHandler(url,POST)
  def putRequestHandler(url: String): Action[AnyContent] = requestHandler(url,PUT)

  private def requestHandler(url: String, method: String): Action[AnyContent] = Action.async { implicit request =>
    findById(dataRepository)(DataIdModel(request.uri, method)) { stubData =>
      schemaValidation.validateRequestJson(stubData.schemaId, request.body.asJson) {
        Future.successful(returnResponse(stubData))
      }
    }
  }

  private def returnResponse(data: DataModel)(implicit request: Request[_]): Result =
    data.response.fold[Result](Status(data.status)) { body =>
      Status(data.status)(body)
    }
}
